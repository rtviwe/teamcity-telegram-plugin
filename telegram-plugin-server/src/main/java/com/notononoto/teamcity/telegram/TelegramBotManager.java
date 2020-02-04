package com.notononoto.teamcity.telegram;

import com.intellij.openapi.diagnostic.Logger;
import com.notononoto.teamcity.telegram.config.TelegramSettings;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetMeResponse;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.StringUtil;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Manage telegram bot state and concurrent access
 * It's don't good to manage state... At first sight we
 * should move sending code to sendMessage method of TelegramNotificator
 * class. But it's impossible because bot should be active always
 * because new users must send at least one message to bot. Otherwise
 * bot will not be able to send a messages to this users because Telegram
 * prohibits it.
 * We use synchronized methods in this class. Of course, read-write lock
 * can get best performance, but it's not important here.
 */
public class TelegramBotManager {

  private static final Logger LOG = Loggers.SERVER;

  /**
   * Plugin settings
   */
  private TelegramSettings settings;
  /**
   * Request executor
   */
  private volatile TelegramBot bot;

  /**
   * Reload bot if settings changed
   *
   * @param newSettings updated user settings
   */
  public synchronized void reloadIfNeeded(@NotNull TelegramSettings newSettings) {
    if (Objects.equals(newSettings, settings)) {
      LOG.debug("Telegram bot token settings has not changed");
      return;
    }
    LOG.debug("New telegram bot token is received: " +
        StringUtil.truncateStringValueWithDotsAtEnd(newSettings.getBotToken(), 6));
    this.settings = newSettings;
    cleanupBot();
    if (settings.getBotToken() != null && !settings.isPaused()) {
      TelegramBot newBot = createBot(settings);
      addUpdatesListener(newBot);
      bot = newBot;
    }
  }

  /**
   * Send message to client
   *
   * @param chatId  client identifier
   * @param message text to send
   */

  public synchronized void sendMessage(String chatId, @NotNull String message) throws IOException {
    if (bot != null) {

      String[] messages = message.split("(?<=\\G.{4096})");
      for (String m : messages) {
        bot.execute(new SendMessage(chatId, convertText(m)).parseMode(ParseMode.Markdown));
      }
    }
  }

  @Nullable
  public BotInfo requestDescription(@NotNull TelegramSettings settings) {
    TelegramBot bot = createBot(settings);
    GetMeResponse response = bot.execute(new GetMe());
    User user = response.user();
    if (user == null) {
      return null;
    }
    return new BotInfo(user.firstName(), user.username());
  }

  public synchronized void destroy() {
    cleanupBot();
  }

  private void cleanupBot() {
    if (bot != null) {
      bot.removeGetUpdatesListener();
      // make cleanup visible to all methods
      bot = null;
    }
  }

  private TelegramBot createBot(@NotNull TelegramSettings settings) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (settings.isUseProxy()) {
      boolean credentialsAreNotEmpty = !StringUtils.isEmpty(settings.getProxyUsername()) &&
          !StringUtils.isEmpty(settings.getProxyPassword());
      switch (settings.getProxyType()) {
        case HTTP:
          addProxyToOkHttp(settings, builder, Proxy.Type.HTTP);
          if (credentialsAreNotEmpty) {
            builder.proxyAuthenticator((route, response) -> {
              String credential =
                  Credentials.basic(settings.getProxyUsername(), settings.getProxyPassword());
              return response.request().newBuilder()
                  .header("Proxy-Authorization", credential)
                  .build();
            });
          }
          break;
        case SOCKS:
          addProxyToOkHttp(settings, builder, Proxy.Type.SOCKS);
          if (credentialsAreNotEmpty) {
            Authenticator.setDefault(new Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestingHost().equalsIgnoreCase(settings.getProxyServer())) {
                  if (settings.getProxyPort() == getRequestingPort()) {
                    return new PasswordAuthentication(settings.getProxyUsername(), settings.getProxyPassword().toCharArray());
                  }
                }
                return null;
              }
            });
          }
          break;
        case DIRECT:
        default:
          break;
      }
    }
    return createBot(settings, builder);
  }

  private void addProxyToOkHttp(@NotNull TelegramSettings settings, OkHttpClient.Builder builder, Proxy.Type socks) {
    builder.proxy(new Proxy(
        socks, new InetSocketAddress(settings.getProxyServer(), settings.getProxyPort())));
  }

  @NotNull
  private TelegramBot createBot(@NotNull TelegramSettings settings, OkHttpClient.Builder builder) {
    return new TelegramBot.Builder(settings.getBotToken()).okHttpClient(builder.build()).build();
  }

  private void addUpdatesListener(TelegramBot bot) {
    bot.setUpdatesListener(updates -> {
      for (Update update : updates) {
        Message message = update.message();
        Long chatId = message.chat().id();
        SendMessage msg = new SendMessage(chatId,
            "Hello! Your chat id is '" + chatId + "'.\n" +
                "If you want to receive notifications about Teamcity events " +
                "please add this chat id in your Teamcity settings");
        bot.execute(msg);
      }
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    });
  }

  @NotNull
  private String convertText(String text) throws IOException {
    StringBuffer sb = new StringBuffer();
    Pattern p = Pattern.compile("(\\w[0x])([\\da-f]{4,5})", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);

    while (m.find()) {
      int hex = Integer.parseInt(m.group(2), 16);
      String s = new String(Character.toChars(hex));
      m.appendReplacement(sb, s);
    }
    m.appendTail(sb);
    return sb.toString();
  }

}

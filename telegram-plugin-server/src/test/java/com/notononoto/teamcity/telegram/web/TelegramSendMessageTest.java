package com.notononoto.teamcity.telegram.web;

import com.notononoto.teamcity.telegram.TelegramBotManager;
import com.notononoto.teamcity.telegram.config.TelegramSettings;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TelegramSendMessageTest {
  @Test
  public void TelegramSendMessageTest() throws IOException {
    TelegramSettingsBean bean = new TelegramSettingsBean(new TelegramSettings());
    bean.setBotToken("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY");
    bean.setPaused(false);
    bean.setUseProxy(false);

    TelegramSettings settings = bean.toSettings();
    assertEquals("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY", settings.getBotToken());
    assertFalse(settings.isPaused());
    assertFalse(settings.isUseProxy());

    TelegramBotManager bot = new TelegramBotManager();
    bot.reloadIfNeeded(settings);

    final String fire = new String(Character.toChars(0x1F525));
    final String front_facing_baby_chick = new String(Character.toChars(0x1F425));
    bot.sendMessage(132713020, fire + " Hello *Hello* [GitHub](http://tfs.argusnet.net:8080/viewType.html?buildTypeId=Integral_IntegralSetup_IntegralSetupInternalRelease)" + front_facing_baby_chick);

  }

  @NotNull
  private String textFile (String nameFile) throws IOException {

    FileReader fr = new FileReader(nameFile);
    Scanner scan = new Scanner(fr);
    String text = new String();
    while (scan.hasNextLine()) {
      text=scan.nextLine();
    }
    fr.close();
    return text;
  }

  @Test
  public void TelegramSendMessageFromFileTest() throws IOException {
    TelegramSettingsBean bean = new TelegramSettingsBean(new TelegramSettings());
    bean.setBotToken("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY");
    bean.setPaused(false);
    bean.setUseProxy(false);

    TelegramSettings settings = bean.toSettings();
    assertEquals("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY", settings.getBotToken());
    assertFalse(settings.isPaused());
    assertFalse(settings.isUseProxy());

    TelegramBotManager bot = new TelegramBotManager();
    bot.reloadIfNeeded(settings);


    bot.sendMessage(132713020, textFile("file.txt") );

  }
}

//57269270 Kostya
//132713020 Lesha
package com.notononoto.teamcity.telegram.web;

//import org.apache.log4j.*;

import com.notononoto.teamcity.telegram.TelegramBotManager;
import com.notononoto.teamcity.telegram.config.TelegramSettings;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TelegramSendMessageTest {
 /* @Test
  public void TelegramSendMessageTest() {
    TelegramSettingsBean bean = new TelegramSettingsBean(new TelegramSettings());
    bean.setBotToken("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY");
    bean.setPaused(true);
    bean.setUseProxy(true);

    TelegramSettings settings = bean.toSettings();
    assertEquals("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY", settings.getBotToken());
    assertTrue(settings.isPaused());
    assertTrue(settings.isUseProxy());



    TelegramBotManager bot = new TelegramBotManager();
    bot.reloadIfNeeded(settings);
    bot.sendMessage( 132713020,"Hello *World!*");
  } */

  @Test
  public void TelegramSendMessageTest() {
    TelegramSettingsBean bean = new TelegramSettingsBean(new TelegramSettings());
    bean.setBotToken("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY");
    bean.setPaused(true);
    bean.setUseProxy(false);

    TelegramSettings settings = bean.toSettings();
    assertEquals("472427726:AAEf2rUEIcJhI_enklkEmE0W2jK7oTSgMrY", settings.getBotToken());
    assertTrue(settings.isPaused());
    Assert.assertFalse(settings.isUseProxy());

    TelegramBotManager bot = new TelegramBotManager();
    bot.reloadIfNeeded(settings);

    bot.sendMessage(132713020, "Hello *World!*");
  }
}
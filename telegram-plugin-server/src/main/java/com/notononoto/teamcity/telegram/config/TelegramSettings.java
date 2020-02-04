package com.notononoto.teamcity.telegram.config;

import java.net.Proxy;
import java.util.Objects;


/** Plugin settings */
public class TelegramSettings {

  private String botToken;
  /** Bot state */
  private boolean paused;
  private boolean useProxy;
  private Proxy.Type proxyType;
  private String proxyServer;
  private Integer proxyPort;
  private String proxyUsername;
  private String proxyPassword;

  public TelegramSettings() {
  }

  public TelegramSettings(TelegramSettings settings) {
    botToken = settings.getBotToken();
    paused = settings.isPaused();
    useProxy = settings.isUseProxy();
    proxyType = settings.getProxyType();
    proxyServer = settings.getProxyServer();
    proxyPort = settings.getProxyPort();
    proxyUsername = settings.getProxyUsername();
    proxyPassword = settings.getProxyPassword();
  }

  public String getBotToken() {
    return botToken;
  }

  public void setBotToken(String botToken) {
    this.botToken = botToken;
  }

  public boolean isUseProxy() {
    return useProxy;
  }

  public void setUseProxy(boolean useProxy) {
    this.useProxy = useProxy;
  }

  public Proxy.Type getProxyType() {
    return proxyType;
  }

  public void setProxyType(Proxy.Type proxyType) {
    this.proxyType = proxyType;
  }

  public String getProxyServer() {
    return proxyServer;
  }

  public void setProxyServer(String proxyServer) {
    this.proxyServer = proxyServer;
  }

  public Integer getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(Integer proxyPort) {
    this.proxyPort = proxyPort;
  }

  public String getProxyUsername() {
    return proxyUsername;
  }

  public void setProxyUsername(String proxyUsername) {
    this.proxyUsername = proxyUsername;
  }

  public String getProxyPassword() {
    return proxyPassword;
  }

  public void setProxyPassword(String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TelegramSettings that = (TelegramSettings) o;
    return useProxy == that.useProxy &&
            paused == that.paused &&
            proxyType == that.proxyType &&
            Objects.equals(botToken, that.botToken) &&
            Objects.equals(proxyServer, that.proxyServer) &&
            Objects.equals(proxyPort, that.proxyPort) &&
            Objects.equals(proxyUsername, that.proxyUsername) &&
            Objects.equals(proxyPassword, that.proxyPassword);
  }

  @Override
  public int hashCode() {
    return Objects.hash(botToken, useProxy, proxyType, proxyServer, proxyPort,
            proxyUsername, proxyPassword, paused);
  }
}

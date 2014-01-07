package org.webframework.system.login.bean;

import org.webframework.WMap;
import org.webframework.cache.platform.Cache;
import org.webframework.system.SysContext;

public class SystemConfig
{
  private static SystemConfig systemConfig = new SystemConfig();

  public static SystemConfig getInstance()
  {
    return systemConfig;
  }

  public String get(String paramId) {
    WMap systemConfig = null;
    try {
      systemConfig = (WMap)Cache.get("_Web_key_cache_init_config_system");
      if (systemConfig == null) {
        SysContext.refreshSystemConfig();
        systemConfig = (WMap)Cache.get("_Web_key_cache_init_config_system");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (systemConfig != null) {
      return systemConfig.getString(paramId);
    }
    return null;
  }
  public String getUserAvatar() {
    return get("user_avatar");
  }
  public String getStyleTheme() {
    return get("style_theme");
  }
  public String getStyleFontSize() {
    return get("style_font_size");
  }
  public String getStyleFontFamily() {
    return get("style_font_family");
  }
  public String getCacheOrgan() {
    return get("cache_organ");
  }
  public String getLogBase() {
    return get("log_base");
  }
  public String getLogRequest() {
    return get("log_request");
  }
  public String getLogCookie() {
    return get("log_cookie");
  }
  public String getLogSql() {
    return get("log_sql");
  }
  public String getLogException() {
    return get("log_exception");
  }
  public String getLogLogin() {
    return get("log_login");
  }
  public String getLogWriteInterval() {
    return get("log_write_interval");
  }
  public String getLogWriteAmountPer() {
    return get("log_write_amount_per");
  }
}
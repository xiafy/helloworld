package org.webframework.system.login.bean;

import javax.servlet.http.HttpSession;
import org.webframework.WMap;
import org.webframework.system.SysContext;

public class UserConfig
{
  private static UserConfig userConfig = new UserConfig();
  private HttpSession session;

  private UserConfig()
  {
  }

  public UserConfig(HttpSession session)
  {
    this.session = session;
  }

  public static UserConfig getInstance() {
    return userConfig;
  }

  public String get(String paramId) {
    WMap userConfig = null;
    if (this.session == null)
      userConfig = SysContext.getUserConfig();
    else {
      userConfig = SysContext.getUserConfig(this.session);
    }
    if (userConfig != null) {
      return userConfig.getString(paramId);
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
}
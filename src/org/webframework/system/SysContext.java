package org.webframework.system;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.cache.platform.Cache;
import org.webframework.system.login.User;
import org.webframework.system.login.bean.RequestContext;
import org.webframework.system.login.bean.SystemConfig;
import org.webframework.system.login.bean.UserConfig;

public class SysContext
{
  private static ThreadLocal aclUserHolder = new ThreadLocal();

  public static void setSysUser(HttpSession session, User user)
  {
    setToSession(session, "_Web_key_user", user);
  }
  public static User getSysUser(HttpSession session) {
    return (User)getFromSession(session, "_Web_key_user");
  }

  public static void setSysUser(User user) {
    setToLocal("_Web_key_user", user);
  }
  public static User getSysUser() {
    return (User)getFromLocal("_Web_key_user");
  }

  public static void setRequestContext(HttpServletRequest request, RequestContext requestContext)
  {
    setToRequest(request, "_Web_key_request_context", requestContext);
  }
  public static RequestContext getRequestContext(HttpServletRequest request) {
    return (RequestContext)getFromRequest(request, "_Web_key_request_context");
  }

  public static void setRequestContext(RequestContext requestContext) {
    setToLocal("_Web_key_request_context", requestContext);
  }
  public static RequestContext getRequestContext() {
    return (RequestContext)getFromLocal("_Web_key_request_context");
  }

  public static void refreshSystemConfig()
  {
    WMap result = new WMap();
    String sql = "SELECT PARAM_ID,PARAM_VALUE FROM SYS_CS T";
    try {
      List list = DB.getMapList(sql);
      int len = list.size();

      for (int i = 0; i < len; i++) {
        WMap map = (WMap)list.get(i);
        result.put(map.get("paramId"), map.get("paramValue"));
      }
      Cache.put("_Web_key_cache_init_config_system", result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static WMap getSystemConfig()
  {
    try {
      WMap m = (WMap)Cache.get("_Web_key_cache_init_config_system");
      return (WMap)Cache.get("_Web_key_cache_init_config_system");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  public static SystemConfig getSystemConfigBean() {
    return SystemConfig.getInstance();
  }

  public static WMap refreshUserConfig(HttpSession session)
  {
    User user = getSysUser(session);
    WMap userConfig = null;
    if (user != null) {
      userConfig = getUserConfig(session);
      if (userConfig == null) {
        userConfig = new WMap();
        setUserConfig(session, userConfig);
      }
      userConfig.clear();

      String sql = "SELECT PARAM_ID,PARAM_VALUE FROM SYS_CZRY T WHERE T.CZRY_DM=?";
      Ps ps = new Ps();
      ps.addString(user.getCzryDm());
      try {
        List list = DB.getMapList(sql, ps);
        int len = list.size();

        for (int i = 0; i < len; i++) {
          WMap map = (WMap)list.get(i);
          userConfig.put(map.get("paramId"), map.get("paramValue"));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return userConfig;
  }
  public static WMap refreshUserConfig() {
    WMap userConfig = getUserConfig();
    User user = getSysUser();
    if ((userConfig != null) && (user != null)) {
      userConfig.clear();

      String sql = "SELECT PARAM_ID,PARAM_VALUE FROM SYS_CZRY_CS T WHERE T.CZRY_DM=?";
      Ps ps = new Ps();
      ps.addString(user.getCzryDm());
      try {
        List list = DB.getMapList(sql, ps);
        int len = list.size();

        for (int i = 0; i < len; i++) {
          WMap map = (WMap)list.get(i);
          userConfig.put(map.get("paramId"), map.get("paramValue"));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return userConfig;
  }

  public static void setUserConfig(HttpSession session, WMap userConfig)
  {
    setToSession(session, "_Web_key_cache_init_config_user", userConfig);
  }
  public static WMap getUserConfig(HttpSession session) {
    return (WMap)getFromSession(session, "_Web_key_cache_init_config_user");
  }
  public static UserConfig getUserConfigBean(HttpSession session) {
    return new UserConfig(session);
  }

  public static void setUserConfig(WMap userConfig) {
    setToLocal("_Web_key_cache_init_config_user", userConfig);
  }
  public static WMap getUserConfig() {
    return (WMap)getFromLocal("_Web_key_cache_init_config_user");
  }
  public static UserConfig getUserConfigBean() {
    return UserConfig.getInstance();
  }

  public static void reset(HttpSession session)
  {
    session.removeAttribute("_Web_key_main");

    aclUserHolder.remove();
  }

  private static void setToLocal(String key, Object obj) {
    WMap map = (WMap)aclUserHolder.get();
    if (map == null) {
      map = new WMap();
      aclUserHolder.set(map);
    }
    map.set(key, obj);
  }

  private static Object getFromLocal(String key) {
    WMap map = (WMap)aclUserHolder.get();
    if (map != null) {
      return map.get(key);
    }
    return null;
  }

  private static void setToSession(HttpSession session, String key, Object obj) {
    WMap map = (WMap)session.getAttribute("_Web_key_main");
    if (map == null) {
      map = new WMap();
      session.setAttribute("_Web_key_main", map);
    }
    map.set(key, obj);
  }

  private static Object getFromSession(HttpSession session, String key) {
    WMap map = (WMap)session.getAttribute("_Web_key_main");
    if (map != null) {
      return map.get(key);
    }
    return null;
  }

  private static void setToRequest(HttpServletRequest request, String key, Object obj) {
    WMap map = (WMap)request.getAttribute("_Web_key_main");
    if (map == null) {
      map = new WMap();
      request.setAttribute("_Web_key_main", map);
    }
    map.set(key, obj);
  }

  private static Object getFromRequest(HttpServletRequest request, String key) {
    WMap map = (WMap)request.getAttribute("_Web_key_main");
    if (map != null) {
      return map.get(key);
    }
    return null;
  }
}
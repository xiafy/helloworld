package org.webframework.system.log.track;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.cache.platform.Cache;
import org.webframework.system.SysContext;
import org.webframework.system.SysUtils;
import org.webframework.system.index.LogInitDao;
import org.webframework.system.login.bean.RequestContext;
import org.webframework.system.manage.entries.PathTable;
import org.webframework.system.manage.tree.PathUtils;

public class LogUtils
{
  public static long defaultInterval = 30L;
  public static Object synTracksObject = new Object();
  public static Collection excludeSqlPool = new HashSet();

  public static long getInterval()
  {
    long interval = defaultInterval;
    try {
      WMap config = (WMap)Cache.get("_Web_key_cache_log_res_config");
      if (config == null) {
        refreshResLogConfig();
        config = (WMap)Cache.get("_Web_key_cache_log_res_config");
      }
      interval = ((WMap)config.get("")).getLong("interval");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return interval;
  }

  public static Runnable[] getCachedTracks() {
    Object logs = null;
    synchronized (synTracksObject) {
      try {
        logs = Cache.get("_Web_key_cache_log_collection");
        Cache.remove("_Web_key_cache_log_collection");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (logs != null) {
      List list = (List)logs;
      return (Runnable[])list.toArray(new Runnable[list.size()]);
    }
    return null;
  }

  public static void addTrack(Runnable log) {
    if (log != null)
      synchronized (synTracksObject) {
        try {
          Object obj = Cache.get("_Web_key_cache_log_collection");
          List logs = null;
          if (obj == null)
            logs = new ArrayList();
          else {
            logs = (List)obj;
          }
          logs.add(log);
          Cache.put("_Web_key_cache_log_collection", logs);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
  }

  public static boolean putToExcludeSqlPool(String sql)
  {
    return excludeSqlPool.add(sql);
  }

  public static boolean isExcludeSql(String sql) {
    return excludeSqlPool.contains(sql);
  }

  public static String getUserIp(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  public static String getNextLogId() {
    String logId = null;
    try {
      logId = SysUtils.getSeqNextValue("SEQ_LOG_ID");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return logId;
  }

  public static boolean isTrack(HttpServletRequest request) {
    RequestContext requestContext = SysContext.getRequestContext(request);
    String zyDm = requestContext.getZyDm();
    zyDm = zyDm == null ? "" : zyDm;
    try {
      WMap config = (WMap)Cache.get("_Web_key_cache_log_res_config");
      if (config == null) {
        refreshResLogConfig();
        config = (WMap)Cache.get("_Web_key_cache_log_res_config");
      }
      WMap resConfig = (WMap)config.get(zyDm);
      if (resConfig != null) {
        requestContext.setTrackBase("1".equals(resConfig.getString("base")));
        requestContext.setTrackRequest("1".equals(resConfig.getString("request")));
        requestContext.setTrackCookie("1".equals(resConfig.getString("cookie")));
        requestContext.setTrackSql("1".equals(resConfig.getString("sql")));
        requestContext.setTrackException("1".equals(resConfig.getString("exception")));
      }
      WMap defaultConfig = (WMap)config.get("");
      if (defaultConfig != null) {
        requestContext.setTrackLogin("1".equals(defaultConfig.getString("login")));
        if (resConfig == null) {
          requestContext.setTrackBase("1".equals(defaultConfig.getString("base")));
          requestContext.setTrackRequest("1".equals(defaultConfig.getString("request")));
          requestContext.setTrackCookie("1".equals(defaultConfig.getString("cookie")));
          requestContext.setTrackSql("1".equals(defaultConfig.getString("sql")));
          requestContext.setTrackException("1".equals(defaultConfig.getString("exception")));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if ((requestContext.isTrackRequest()) || (requestContext.isTrackCookie()) || (requestContext.isTrackSql())) {
      requestContext.setTrackBase(true);
    }
    if (requestContext.isTrackBase()) {
      requestContext.setLogId(getNextLogId());
    }
    SysContext.setRequestContext(requestContext);
    return requestContext.isTrackBase();
  }

  public static void refreshResLogConfig() throws Exception {
    WMap result = new WMap();
    WMap pool = new WMap();
    WMap map = null;

    StringBuffer sql = new StringBuffer();

    WMap defaultConfig = new LogInitDao().getDefaultParams();
    result.put("", defaultConfig);

    sql.setLength(0);
    sql.append("SELECT T.ID, T.BASE, T.REQUEST, T.COOKIE, T.EXCEPTION, T.SQL ");
    sql.append("  FROM SYS_LOG T, QX_GNMK_ZY T1 ");
    sql.append(" WHERE T.ID = T1.ZY_DM ");
    List list = DB.getMapList(sql.toString());
    int len = list.size();
    for (int i = 0; i < len; i++) {
      map = (WMap)list.get(i);
      result.put(map.getString("id"), map);
    }

    sql.setLength(0);
    sql.append("SELECT T.ZY_DM,T.GNMK_DM ");
    sql.append("  FROM QX_GNMK_ZY T ");
    sql.append(" WHERE NOT EXISTS (SELECT T1.ID FROM SYS_LOG T1 WHERE T.ZY_DM = T1.ID) ");
    List resList = DB.getMapList(sql.toString());

    sql.setLength(0);
    sql.append("SELECT T1.ID, T1.REQUEST, T1.COOKIE, T1.EXCEPTION, T1.SQL ");
    sql.append("  FROM SYS_LOG T1, ");
    sql.append("       (SELECT T2.GNMK_DM ");
    sql.append("          FROM QX_GNMK T2 ");
    sql.append("\t\t  WHERE ");
    sql.append(PathUtils.getLowerLevelById(
      new PathTable("QX_GNMK").setId("(SELECT T3.GNMK_DM FROM QX_GNMK_ZY T3 WHERE T3.ZY_DM=?)"), "T2.GNMK_DM"));
    sql.append(" ) T4 ");
    sql.append(" WHERE T1.ID = T4.GNMK_DM ");
    len = resList.size();
    Ps ps = null;

    for (int i = 0; i < len; i++) {
      map = (WMap)resList.get(i);
      String redId = map.getString("zyDm");
      String gnmkDm = map.getString("gnmkDm");

      if (pool.containsKey(gnmkDm)) {
        result.put(redId, pool.get(gnmkDm));
      } else {
        ps = new Ps();
        ps.addString(redId);
        List parConfigList = DB.getMapList(sql.toString(), ps);
        if (parConfigList.isEmpty()) {
          result.put(redId, defaultConfig);
          pool.put(gnmkDm, defaultConfig);
        } else {
          result.put(redId, parConfigList.get(0));
          pool.put(gnmkDm, parConfigList.get(0));
        }
      }
    }
    Cache.put("_Web_key_cache_log_res_config", result);

    SysContext.refreshSystemConfig();
  }
}
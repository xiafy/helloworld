package org.webframework.system.log.track;

import javax.servlet.http.HttpServletRequest;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.system.SysContext;
import org.webframework.system.login.bean.RequestContext;

public class TrackCookie
  implements Runnable
{
  private String logId;
  private String cookie;

  public static void track(HttpServletRequest request)
  {
    RequestContext requestContext = SysContext.getRequestContext(request);
    if ((requestContext != null) && (requestContext.isTrackCookie())) {
      TrackCookie trackCookie = new TrackCookie();
      trackCookie.setLogId(requestContext.getLogId());
      trackCookie.setCookie(request.getHeader("Cookie"));
      LogUtils.addTrack(trackCookie);
    }
  }

  public void run() {
    log();
  }

  public void log() {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO LOG_COOKIE ");
    sql.append("  (LOG_ID, COOKIE) ");
    sql.append("VALUES ");
    sql.append("  (?, ?) ");
    LogUtils.putToExcludeSqlPool(sql.toString());

    Ps ps = new Ps();
    ps.addString(this.logId);
    if ((this.cookie != null) && (this.cookie.length() > 3000)) {
      this.cookie = this.cookie.substring(0, 3000);
    }
    ps.addString(this.cookie);
    try {
      DB.update(sql.toString(), ps);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getLogId() {
    return this.logId;
  }

  public void setLogId(String logId) {
    this.logId = logId;
  }

  public String getCookie() {
    return this.cookie;
  }

  public void setCookie(String cookie) {
    this.cookie = cookie;
  }
}
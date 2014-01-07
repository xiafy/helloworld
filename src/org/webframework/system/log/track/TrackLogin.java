package org.webframework.system.log.track;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.system.SysContext;
import org.webframework.system.SysUtils;
import org.webframework.system.login.User;
import org.webframework.system.login.bean.RequestContext;

public class TrackLogin
  implements Runnable
{
  private String logId;
  private String czryDm;
  private String userIp;
  private String msg;
  private String type;
  private String time;

  public static void track(HttpServletRequest request, String msg, String type)
  {
    RequestContext requestContext = SysContext.getRequestContext(request);
    if ((requestContext != null) && (requestContext.isTrackLogin())) {
      User user = SysContext.getSysUser(request.getSession());
      if (user != null) {
        TrackLogin trackLogin = new TrackLogin();
        trackLogin.setLogId(requestContext.getLogId());
        trackLogin.setCzryDm(user.getCzryDm());
        trackLogin.setUserIp(LogUtils.getUserIp(request));
        trackLogin.setMsg(msg);
        trackLogin.setType(type);
        trackLogin.setTime(SysUtils.getTimestampDb());
        LogUtils.addTrack(trackLogin);
      }
    }
  }

  public static void trackFail(HttpServletRequest request, String czryDm, String msg) {
    RequestContext requestContext = SysContext.getRequestContext(request);
    if ((requestContext != null) && (requestContext.isTrackLogin())) {
      requestContext.setCzryDm(czryDm);
      if (!requestContext.isTrackBase()) {
        requestContext.setTrackBase(true);
        TrackBase.track(request);
      }
      TrackLogin trackLogin = new TrackLogin();
      trackLogin.setLogId(requestContext.getLogId());
      trackLogin.setCzryDm(requestContext.getCzryDm());
      trackLogin.setUserIp(LogUtils.getUserIp(request));
      trackLogin.setMsg(msg);
      trackLogin.setType("0");
      trackLogin.setTime(SysUtils.getTimestampDb());
      LogUtils.addTrack(trackLogin);
    }
  }

  public static void trackSessionDestroyed(HttpSession session) {
    User user = SysContext.getSysUser(session);
    if (user != null) {
      String logId = TrackBase.trackSessionDestroyed(user.getCzryDm());
      TrackLogin trackLogin = new TrackLogin();
      trackLogin.setLogId(logId);
      trackLogin.setCzryDm(user.getCzryDm());
      trackLogin.setMsg("会话过期退出");
      trackLogin.setType("2");
      trackLogin.setTime(SysUtils.getTimestampDb());
      LogUtils.addTrack(trackLogin);
    }
  }

  public void run() {
    log();
  }

  public void log() {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO LOG_LOGIN ");
    sql.append("  (LOG_ID, CZRY_DM, USER_IP, MSG, TYPE, TIME) ");
    sql.append("VALUES ");
    sql.append("  (?, ?, ?, ?, ?, ").append(DB.getSyntaxDialect().string2Timestamp("?")).append(") ");
    LogUtils.putToExcludeSqlPool(sql.toString());

    Ps ps = new Ps();
    ps.addString(this.logId);
    ps.addString(this.czryDm);
    ps.addString(this.userIp);
    ps.addString(this.msg);
    ps.addString(this.type);
    ps.addString(this.time);
    try {
      DB.update(sql.toString(), ps);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getCzryDm() {
    return this.czryDm;
  }

  public void setCzryDm(String czryDm) {
    this.czryDm = czryDm;
  }

  public String getUserIp() {
    return this.userIp;
  }

  public void setUserIp(String userIp) {
    this.userIp = userIp;
  }

  public String getMsg() {
    return this.msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTime() {
    return this.time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getLogId() {
    return this.logId;
  }

  public void setLogId(String logId) {
    this.logId = logId;
  }
}
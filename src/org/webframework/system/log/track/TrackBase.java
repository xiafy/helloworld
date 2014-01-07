package org.webframework.system.log.track;

import javax.servlet.http.HttpServletRequest;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.system.SysContext;
import org.webframework.system.SysUtils;
import org.webframework.system.login.bean.RequestContext;

public class TrackBase
  implements Runnable
{
  private String logId;
  private String czryDm;
  private String userIp;
  private String serverIp;
  private String serverPort;
  private String userAgent;
  private String uri;
  private String zyDm;
  private String isAjax;
  private String beginTime;
  private String endTime;

  public static void track(HttpServletRequest request)
  {
    boolean isTrack = LogUtils.isTrack(request);
    if (isTrack) {
      TrackBase trackBase = new TrackBase();
      RequestContext requestContext = SysContext.getRequestContext(request);
      trackBase.setLogId(requestContext.getLogId());
      trackBase.setCzryDm(requestContext.getCzryDm());
      trackBase.setUserIp(LogUtils.getUserIp(request));
      trackBase.setServerIp(request.getServerName());
      trackBase.setServerPort(String.valueOf(request.getServerPort()));
      trackBase.setUserAgent(request.getHeader("user-agent"));
      trackBase.setUri(requestContext.getRequestUrl());
      trackBase.setZyDm(requestContext.getZyDm());
      trackBase.setBeginTime(SysUtils.getTimestampDb());
      String ajaxHeader = request.getHeader("X-Requested-With");
      if ((ajaxHeader != null) && (ajaxHeader.equalsIgnoreCase("XMLHttpRequest")))
        trackBase.setIsAjax("1");
      else {
        trackBase.setIsAjax("0");
      }
      LogUtils.addTrack(trackBase);

      TrackRequest.track(request);
      TrackCookie.track(request);
    }
  }

  public static String trackSessionDestroyed(String czryDm) {
    TrackBase trackBase = new TrackBase();
    String logId = LogUtils.getNextLogId();
    trackBase.setLogId(logId);
    trackBase.setCzryDm(czryDm);
    trackBase.setBeginTime(SysUtils.getTimestampDb());
    trackBase.setIsAjax("0");
    LogUtils.addTrack(trackBase);
    return logId;
  }

  public static void trackEndTime(HttpServletRequest request) {
    RequestContext requestContext = SysContext.getRequestContext(request);
    if ((requestContext != null) && (requestContext.isTrackBase())) {
      TrackBase trackBase = new TrackBase();
      trackBase.setLogId(requestContext.getLogId());
      trackBase.setEndTime(SysUtils.getTimestampDb());
      LogUtils.addTrack(trackBase);
    }
  }

  public void run() {
    log();
  }

  public void log() {
    if (this.endTime == null) {
      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO LOG ");
      sql.append("(");
      sql.append("LOG_ID,CZRY_DM,USER_IP,SERVER_IP,SERVER_PORT,USER_AGENT,URI,ZY_DM,BEGIN_TIME,IS_AJAX,SUCCESS");
      sql.append(") ");
      sql.append("VALUES ");
      sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ").append(DB.getSyntaxDialect().string2Timestamp("?")).append(", ?,'1')");
      LogUtils.putToExcludeSqlPool(sql.toString());

      Ps ps = new Ps();
      ps.addString(this.logId);
      ps.addString(this.czryDm);
      ps.addString(this.userIp);
      ps.addString(this.serverIp);
      ps.addString(this.serverPort);
      if ((this.uri != null) && (this.uri.length() > 1000)) {
        this.uri = this.uri.substring(0, 1000);
      }
      if ((this.userAgent != null) && (this.userAgent.length() > 500)) {
        this.userAgent = this.userAgent.substring(0, 500);
      }
      ps.addString(this.userAgent);
      ps.addString(this.uri);
      ps.addString(this.zyDm);
      ps.addString(this.beginTime);
      ps.addString(this.isAjax);
      try
      {
        DB.update(sql.toString(), ps);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      StringBuffer sql = new StringBuffer();
      sql.append("UPDATE LOG ");
      sql.append("SET ");
      sql.append("END_TIME=").append(DB.getSyntaxDialect().string2Timestamp("?"));
      sql.append(" WHERE ");
      sql.append("LOG_ID=? ");
      LogUtils.putToExcludeSqlPool(sql.toString());

      Ps ps = new Ps();
      ps.addString(this.endTime);
      ps.addString(this.logId);
      try {
        DB.update(sql.toString(), ps);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public String getLogId() {
    return this.logId;
  }

  public void setLogId(String logId) {
    this.logId = logId;
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

  public String getServerIp() {
    return this.serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public String getServerPort() {
    return this.serverPort;
  }

  public void setServerPort(String serverPort) {
    this.serverPort = serverPort;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getUri() {
    return this.uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getZyDm() {
    return this.zyDm;
  }

  public void setZyDm(String zyDm) {
    this.zyDm = zyDm;
  }

  public String getIsAjax() {
    return this.isAjax;
  }

  public void setIsAjax(String isAjax) {
    this.isAjax = isAjax;
  }

  public String getBeginTime() {
    return this.beginTime;
  }

  public void setBeginTime(String beginTime) {
    this.beginTime = beginTime;
  }

  public String getEndTime() {
    return this.endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }
}
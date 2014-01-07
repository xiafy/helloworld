package org.webframework.system.login.bean;

import java.io.Serializable;
import org.webframework.WMap;

public class RequestContext
  implements Serializable
{
  private String czryDm;
  private String logId;
  private String requestUrl;
  private String zyDm;
  private boolean trackBase;
  private boolean trackRequest;
  private boolean trackCookie;
  private boolean trackSql;
  private boolean trackException;
  private boolean trackLogin;
  private WMap extMap;

  public String getLogId()
  {
    return this.logId;
  }
  public void setLogId(String logId) {
    this.logId = logId;
  }
  public boolean isTrackBase() {
    return this.trackBase;
  }
  public void setTrackBase(boolean trackBase) {
    this.trackBase = trackBase;
  }
  public boolean isTrackRequest() {
    return this.trackRequest;
  }
  public void setTrackRequest(boolean trackRequest) {
    this.trackRequest = trackRequest;
  }
  public boolean isTrackCookie() {
    return this.trackCookie;
  }
  public void setTrackCookie(boolean trackCookie) {
    this.trackCookie = trackCookie;
  }
  public boolean isTrackSql() {
    return this.trackSql;
  }
  public void setTrackSql(boolean trackSql) {
    this.trackSql = trackSql;
  }
  public boolean isTrackException() {
    return this.trackException;
  }
  public void setTrackException(boolean trackException) {
    this.trackException = trackException;
  }
  public String getZyDm() {
    return this.zyDm;
  }
  public void setZyDm(String zyDm) {
    this.zyDm = zyDm;
  }
  public boolean isTrackLogin() {
    return this.trackLogin;
  }
  public void setTrackLogin(boolean trackLogin) {
    this.trackLogin = trackLogin;
  }
  public String getCzryDm() {
    return this.czryDm;
  }
  public void setCzryDm(String czryDm) {
    this.czryDm = czryDm;
  }
  public WMap getExtMap() {
    return this.extMap;
  }
  public void setExtMap(WMap extMap) {
    this.extMap = extMap;
  }
  public String getRequestUrl() {
    return this.requestUrl;
  }
  public void setRequestUrl(String requestUrl) {
    this.requestUrl = requestUrl;
  }
}
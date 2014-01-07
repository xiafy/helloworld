package org.webframework.system.log.track;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.system.SysContext;
import org.webframework.system.login.bean.RequestContext;

public class TrackRequest
  implements Runnable
{
  private String logId;
  private String characterEncoding;
  private long contentLength;
  private String params;
  private String contentType;
  private String protocol;
  private String scheme;
  private String queryString;
  private String proxyIp;
  private String url;
  private String context;
  private String accept;
  private String referer;
  private String acceptLanguage;
  private String acceptEncoding;
  private String connection;

  public static void track(HttpServletRequest request)
  {
    RequestContext requestContext = SysContext.getRequestContext(request);
    if ((requestContext != null) && (requestContext.isTrackRequest())) {
      TrackRequest trackRequest = new TrackRequest();
      trackRequest.setLogId(requestContext.getLogId());
      trackRequest.setCharacterEncoding(request.getCharacterEncoding());
      trackRequest.setContentLength(request.getContentLength());

      Map params = new LinkedHashMap();
      Enumeration names = request.getParameterNames();

      while (names.hasMoreElements()) {
        String key = (String)names.nextElement();
        String[] values = request.getParameterValues(key);
        String value = null;
        if (values != null) {
          if (values.length == 1)
            value = values[0];
          else {
            for (int i = 0; i < values.length; i++) {
              if (value == null) value = "[" + i + "]" + values[i]; else
                value = value + ' ' + "[" + i + "]" + values[i];
            }
          }
        }
        params.put(key, value);
      }
      trackRequest.setParams(params.toString());
      trackRequest.setContentType(request.getContentType());
      trackRequest.setProtocol(request.getProtocol());
      trackRequest.setScheme(request.getScheme());
      trackRequest.setQueryString(request.getQueryString());
      String userIp = request.getRemoteAddr();
      if (!userIp.equals(LogUtils.getUserIp(request))) {
        trackRequest.setProxyIp(userIp);
      }
      trackRequest.setUrl(request.getRequestURL().toString());
      trackRequest.setContext(request.getContextPath());
      trackRequest.setAccept(request.getHeader("accept"));
      trackRequest.setReferer(request.getHeader("referer"));
      trackRequest.setAcceptLanguage(request.getHeader("accept-language"));
      trackRequest.setAcceptEncoding(request.getHeader("accept-encoding"));
      trackRequest.setConnection(request.getHeader("connection"));
      LogUtils.addTrack(trackRequest);
    }
  }

  public void run() {
    log();
  }

  public void log() {
    StringBuffer sql = new StringBuffer();

    sql.append("INSERT INTO LOG_REQUEST ");
    sql.append("  (LOG_ID,CHARACTER_ENCODING,CONTENT_LENGTH,CONTENT_TYPE, ");
    sql.append("   PROTOCOL,SCHEME,QUERY_STRING,PROXY_IP,URL,CONTEXT, ");
    sql.append("   ACCEPT,REFERER,ACCEPT_LANGUAGE,ACCEPT_ENCODING,CONNECTION,PARAMS ");
    sql.append("   ) ");
    sql.append("VALUES ");
    sql.append("  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
    LogUtils.putToExcludeSqlPool(sql.toString());

    Ps ps = new Ps();
    ps.addString(this.logId);
    ps.addString(this.characterEncoding);
    ps.addLong(this.contentLength);
    ps.addString(this.contentType);
    ps.addString(this.protocol);
    ps.addString(this.scheme);
    ps.addString(this.queryString);
    ps.addString(this.proxyIp);
    ps.addString(this.url);
    ps.addString(this.context);
    ps.addString(this.accept);
    ps.addString(this.referer);
    ps.addString(this.acceptLanguage);
    ps.addString(this.acceptEncoding);
    ps.addString(this.connection);
    ps.addString(this.params);
    try
    {
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

  public String getCharacterEncoding() {
    return this.characterEncoding;
  }

  public void setCharacterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
  }

  public long getContentLength() {
    return this.contentLength;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  public String getContentType() {
    return this.contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getProtocol() {
    return this.protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getScheme() {
    return this.scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  public String getQueryString() {
    return this.queryString;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  public String getProxyIp() {
    return this.proxyIp;
  }

  public void setProxyIp(String proxyIp) {
    this.proxyIp = proxyIp;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getContext() {
    return this.context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String getAccept() {
    return this.accept;
  }

  public void setAccept(String accept) {
    this.accept = accept;
  }

  public String getReferer() {
    return this.referer;
  }

  public void setReferer(String referer) {
    this.referer = referer;
  }

  public String getAcceptLanguage() {
    return this.acceptLanguage;
  }

  public void setAcceptLanguage(String acceptLanguage) {
    this.acceptLanguage = acceptLanguage;
  }

  public String getAcceptEncoding() {
    return this.acceptEncoding;
  }

  public void setAcceptEncoding(String acceptEncoding) {
    this.acceptEncoding = acceptEncoding;
  }

  public String getConnection() {
    return this.connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  public String getParams() {
    return this.params;
  }

  public void setParams(String params) {
    this.params = params;
  }
}
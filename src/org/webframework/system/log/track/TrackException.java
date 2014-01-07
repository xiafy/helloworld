package org.webframework.system.log.track;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.mvc.AjaxView;
import org.webframework.system.SysContext;
import org.webframework.system.SysUtils;
import org.webframework.system.login.bean.RequestContext;

public class TrackException
  implements Runnable
{
  private String logId;
  private String stack;
  private String params;
  private String time;
  private String type;

  public static void track(HttpServletRequest request, Throwable e)
  {
    RequestContext requestContext = SysContext.getRequestContext(request);
    if ((requestContext != null) && (requestContext.isTrackException())) {
      if (!requestContext.isTrackBase()) {
        requestContext.setTrackBase(true);
        TrackBase.track(request);
      }

      TrackException trackException = new TrackException();
      trackException.setLogId(requestContext.getLogId());

      StringWriter exception = new StringWriter();
      e.printStackTrace(new PrintWriter(exception));
      trackException.setStack(exception.toString());

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
      trackException.setParams(params.toString());

      trackException.setType("0");

      trackException.setTime(SysUtils.getTimestampDb());
      LogUtils.addTrack(trackException);
    }
  }

  public static void trackAjaxWarn(HttpServletRequest request, AjaxView view) {
    if (view.hasError()) {
      RequestContext requestContext = SysContext.getRequestContext(request);
      if ((requestContext != null) && (requestContext.isTrackException())) {
        if (!requestContext.isTrackBase()) {
          requestContext.setTrackBase(true);
          TrackBase.track(request);
        }

        TrackException trackException = new TrackException();
        trackException.setLogId(requestContext.getLogId());
        trackException.setStack(view.getErrorMsg());

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
        trackException.setParams(params.toString());

        trackException.setType("2");

        trackException.setTime(SysUtils.getTimestampDb());
        LogUtils.addTrack(trackException);
      }
    }
  }

  public void run() {
    log();
  }

  public void log()
  {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO LOG_EXCEPTION (LOG_ID, PARAMS, TIME, STACK) VALUES (?, ?, ").append(DB.getSyntaxDialect().string2Timestamp("?")).append(", ?) ");
    LogUtils.putToExcludeSqlPool(sql.toString());

    Ps ps = new Ps();
    ps.addString(this.logId);
    ps.addString(this.params);
    ps.addString(this.time);
    ps.addString(this.stack);
    try {
      DB.update(sql.toString(), ps);
    } catch (Exception e) {
      e.printStackTrace();
    }

    sql.setLength(0);
    sql.append("UPDATE LOG SET SUCCESS=? WHERE LOG_ID=? ");
    LogUtils.putToExcludeSqlPool(sql.toString());
    ps = new Ps();
    ps.addString(this.type);
    ps.addString(this.logId);
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
  public String getStack() {
    return this.stack;
  }
  public void setStack(String stack) {
    this.stack = stack;
  }
  public String getParams() {
    return this.params;
  }
  public void setParams(String params) {
    this.params = params;
  }
  public String getTime() {
    return this.time;
  }
  public void setTime(String time) {
    this.time = time;
  }
  public String getType() {
    return this.type;
  }
  public void setType(String type) {
    this.type = type;
  }
}
package org.webframework.system.log.track;

import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.system.SysContext;
import org.webframework.system.SysUtils;
import org.webframework.system.login.bean.RequestContext;

public class TrackSql
  implements Runnable
{
  private String logId;
  private String sql;
  private String params;
  private int resultCount;
  private long consumeTime;
  private String dataSource;
  private String time;

  public static void track(String sql, Object[] parameters, int resultCount, long consumeTime, String dataSource)
  {
    RequestContext requestContext = SysContext.getRequestContext();
    if ((requestContext != null) && (requestContext.isTrackSql()) && 
      (!LogUtils.isExcludeSql(sql))) {
      TrackSql trackSql = new TrackSql();
      trackSql.setLogId(requestContext.getLogId());
      trackSql.setSql(sql);
      if (parameters != null) {
        StringBuffer params = new StringBuffer();
        int len = parameters.length;
        for (int i = 0; i < len; i++) {
          params.append(parameters[i]);
          if (i + 1 != len) {
            params.append(",");
          }
        }
        trackSql.setParams(params.toString());
      }
      trackSql.setResultCount(resultCount);
      trackSql.setConsumeTime(consumeTime);
      trackSql.setDataSource(dataSource);
      trackSql.setTime(SysUtils.getTimestampDb());
      LogUtils.addTrack(trackSql);
    }
  }

  public static void trackBatch(String[] sql, int[] resultCount, long consumeTime, String dataSource)
  {
    RequestContext requestContext = SysContext.getRequestContext();
    if ((requestContext != null) && (requestContext.isTrackSql())) {
      int len = resultCount.length;
      consumeTime /= len;
      for (int i = 0; i < len; i++)
        if (!LogUtils.isExcludeSql(sql[i])) {
          TrackSql trackSql = new TrackSql();
          trackSql.setLogId(requestContext.getLogId());
          trackSql.setSql(sql[i]);
          trackSql.setResultCount(resultCount[i]);
          trackSql.setConsumeTime(consumeTime);
          trackSql.setDataSource(dataSource);
          trackSql.setTime(SysUtils.getTimestampDb());
          LogUtils.addTrack(trackSql);
        }
    }
  }

  public static void trackBatch(String sql, Ps[] psAry, int[] resultCount, long consumeTime, String dataSource)
  {
    RequestContext requestContext = SysContext.getRequestContext();
    if ((requestContext != null) && (requestContext.isTrackSql()) && 
      (!LogUtils.isExcludeSql(sql))) {
      int len = resultCount.length;
      consumeTime /= len;
      Ps ps = null;
      Object[] parameters = (Object[])null;
      StringBuffer params = new StringBuffer();
      for (int i = 0; i < len; i++) {
        TrackSql trackSql = new TrackSql();
        trackSql.setLogId(requestContext.getLogId());
        trackSql.setSql(sql);
        ps = psAry[i];
        if (ps != null) {
          parameters = ps.getParams();
          if (parameters != null) {
            params.setLength(0);
            int size = parameters.length;
            for (int j = 0; j < size; j++) {
              params.append(parameters[j]);
              if (j + 1 != size) {
                params.append(",");
              }
            }
            trackSql.setParams(params.toString());
          }
        }
        trackSql.setResultCount(resultCount[i]);
        trackSql.setConsumeTime(consumeTime);
        trackSql.setDataSource(dataSource);
        trackSql.setTime(SysUtils.getTimestampDb());
        LogUtils.addTrack(trackSql);
      }
    }
  }

  public void run()
  {
    log();
  }

  public void log() {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO LOG_SQL ");
    sql.append("  (LOG_ID, SQL, PARAMS, RESULT_COUNT, CONSUME_TIME, DATA_SOURCE, TIME) ");
    sql.append("VALUES ");
    sql.append("  (?, ?, ?, ?, ?, ?, ").append(DB.getSyntaxDialect().string2Timestamp("?")).append(") ");
    LogUtils.putToExcludeSqlPool(sql.toString());

    Ps ps = new Ps();
    ps.addString(this.logId);
    ps.addString(this.sql);
    ps.addString(this.params);
    ps.addLong(this.resultCount);
    ps.addLong(this.consumeTime);
    ps.addString(this.dataSource);
    ps.addString(this.time);
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

  public String getSql() {
    return this.sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public String getParams() {
    return this.params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public int getResultCount() {
    return this.resultCount;
  }

  public void setResultCount(int resultCount) {
    this.resultCount = resultCount;
  }

  public long getConsumeTime() {
    return this.consumeTime;
  }

  public void setConsumeTime(long consumeTime) {
    this.consumeTime = consumeTime;
  }

  public String getDataSource() {
    return this.dataSource;
  }

  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  public String getTime() {
    return this.time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
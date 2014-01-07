package org.webframework.db.dialect.syntax;

import org.webframework.DB;
import org.webframework.WMap;
import org.webframework.system.log.track.LogUtils;

public class OracleSyntaxDialect extends SyntaxDialect
{
  public String DATE()
  {
    return "SYSDATE";
  }

  public String TIMESTAMP() {
    return "SYSTIMESTAMP";
  }

  public String getDate() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT TO_CHAR(SYSDATE,'YYYY-MM-DD') W11_DATE FROM DUAL");
    LogUtils.putToExcludeSqlPool(sql.toString());
    String date = null;
    try {
      date = DB.getMap(sql.toString()).getString("w11Date");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return date;
  }

  public String getTime() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT TO_CHAR(SYSDATE,'HH24:MI:SS') W11_TIME FROM DUAL");
    LogUtils.putToExcludeSqlPool(sql.toString());
    String time = null;
    try {
      time = DB.getMap(sql.toString()).getString("w11Time");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return time;
  }

  public String getDateTime() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') W11_DATE_TIME FROM DUAL");
    LogUtils.putToExcludeSqlPool(sql.toString());
    String dateTime = null;
    try {
      dateTime = DB.getMap(sql.toString()).getString("w11DateTime");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dateTime;
  }

  public String getTimestamp() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT TO_CHAR(SYSTIMESTAMP,'YYYY-MM-DD HH24:MI:SS.FF') W11_TIMESTAMP FROM DUAL");
    LogUtils.putToExcludeSqlPool(sql.toString());
    String time = null;
    try {
      time = DB.getMap(sql.toString()).getString("w11Timestamp");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return time;
  }

  public String date2String(String field) {
    return "TO_CHAR(" + field + ",'YYYY-MM-DD')";
  }

  public String time2String(String field) {
    return "TO_CHAR(" + field + ",'HH24:MI:SS')";
  }

  public String dateTime2String(String field) {
    return "TO_CHAR(" + field + ",'YYYY-MM-DD HH24:MI:SS')";
  }

  public String timestamp2String(String field) {
    return "TO_CHAR(" + field + ",'YYYY-MM-DD HH24:MI:SS.FF')";
  }

  public String string2Date(String date) {
    if (!"?".equals(date.trim())) {
      date = "'" + date + "'";
    }
    return "TO_DATE(" + date + ",'YYYY-MM-DD')";
  }

  public String string2Time(String date) {
    if (!"?".equals(date.trim())) {
      date = "'" + date + "'";
    }
    return "TO_DATE(" + date + ",'HH24:MI:SS')";
  }

  public String string2DateTime(String date) {
    if (!"?".equals(date.trim())) {
      date = "'" + date + "'";
    }
    return "TO_DATE(" + date + ",'YYYY-MM-DD HH24:MI:SS')";
  }

  public String string2Timestamp(String date) {
    if (!"?".equals(date.trim())) {
      date = "'" + date + "'";
    }
    return "TO_TIMESTAMP(" + date + ",'YYYY-MM-DD HH24:MI:SS.FF')";
  }

  public String getSeqNextValue(String seqName) throws Exception {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ").append(seqName).append(".NEXTVAL W11_SEQ FROM DUAL ");
    LogUtils.putToExcludeSqlPool(sql.toString());
    String nextValue = "";
    WMap wmap = DB.getMap(sql.toString());
    if (wmap != null) {
      nextValue = wmap.getString("w11Seq");
    }
    return nextValue;
  }

  public String CONCAT() {
    return "||";
  }

  public String SUBSTRING(String str, int start) {
    return "SUBSTR(" + str + "," + start + ")";
  }

  public String SUBSTRING(String str, int start, int end) {
    return "SUBSTR(" + str + "," + start + "," + end + ")";
  }

  public String LENGTH(String str) {
    return "LENGTH(" + str + ")";
  }

  public String string2Number(String str) {
    return "TO_NUMBER(" + str + ")";
  }
}
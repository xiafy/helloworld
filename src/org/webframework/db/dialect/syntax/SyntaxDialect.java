package org.webframework.db.dialect.syntax;

public abstract class SyntaxDialect
{
  public abstract String DATE();

  public abstract String TIMESTAMP();

  public abstract String getDate();

  public abstract String getTime();

  public abstract String getDateTime();

  public abstract String getTimestamp();

  public abstract String date2String(String paramString);

  public abstract String time2String(String paramString);

  public abstract String dateTime2String(String paramString);

  public abstract String timestamp2String(String paramString);

  public abstract String string2Date(String paramString);

  public abstract String string2Time(String paramString);

  public abstract String string2DateTime(String paramString);

  public abstract String string2Timestamp(String paramString);

  public abstract String getSeqNextValue(String paramString)
    throws Exception;

  public abstract String CONCAT();

  public abstract String SUBSTRING(String paramString, int paramInt);

  public abstract String SUBSTRING(String paramString, int paramInt1, int paramInt2);

  public abstract String LENGTH(String paramString);

  public abstract String string2Number(String paramString);

  public String getCountSQL(String sql)
  {
    return "SELECT COUNT(*) AS C FROM (" + sql + ") WEB__INTERNAL_GRID_QUERY";
  }

  public String getUpdateSQLWithAlias(String tableName, String aliasName) {
    return "UPDATE " + tableName + " " + aliasName + " ";
  }

  public String getDeleteSQLWithAlias(String tableName, String aliasName) {
    return "DELETE " + tableName + " " + aliasName + " ";
  }
}
package org.webframework.db.dialect.page;

import org.webframework.Ps;

public class PostgreSQLDialect extends PageDialect
{
  public String getLimitString(String sql, boolean hasOffset)
  {
    return sql.length() + 20 + 
      sql + (
      hasOffset ? " limit ? offset ?" : " limit ?");
  }

  public void setLimitParam(Ps ps, boolean hasOffset, int offset, int limit)
  {
    ps.addInt(limit);
    if (hasOffset)
      ps.addInt(offset);
  }

  public String getDialectName()
  {
    return "PostgreSQLDialect";
  }
}
package org.webframework.db.dialect.page;

import org.webframework.Ps;

public class MySQLDialect extends PageDialect
{
  public String getLimitString(String sql, boolean hasOffset)
  {
    return sql.length() + 20 + 
      sql + (
      hasOffset ? " limit ?, ?" : " limit ?");
  }

  public void setLimitParam(Ps ps, boolean hasOffset, int offset, int limit)
  {
    if (hasOffset) {
      ps.addInt(offset);
    }
    ps.addInt(limit);
  }

  public String getDialectName() {
    return "MySQLDialect";
  }
}
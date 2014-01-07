package org.webframework.db.dialect.page;

import org.webframework.Ps;

public class HSQLDialect extends PageDialect
{
  public String getLimitString(String sql, boolean hasOffset)
  {
    return new StringBuffer(sql.length() + 10)
      .append(sql)
      .insert(sql.toLowerCase().indexOf("select") + 6, hasOffset ? " limit ? ?" : " top ?")
      .toString();
  }

  public void setLimitParam(Ps ps, boolean hasOffset, int offset, int limit) {
    if (hasOffset) {
      ps.addInt(offset);
    }
    ps.addInt(limit);
  }

  public String getDialectName() {
    return "HSQLDialect";
  }
}
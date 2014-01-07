package org.webframework.db.dialect.page;

import org.webframework.Ps;

public abstract class PageDialect
{
  protected String getLimitString(String query, boolean hasOffset)
  {
    throw new UnsupportedOperationException("数据库不支持分页查询");
  }

  public String getLimitString(String query, int offset, int limit) {
    return getLimitString(query, offset > 0);
  }

  protected void setLimitParam(Ps ps, boolean hasOffset, int offset, int limit) {
    throw new UnsupportedOperationException("数据库不支持分页查询");
  }

  public void setLimitParam(Ps ps, int offset, int limit) {
    setLimitParam(ps, offset > 0, offset, limit);
  }

  public String getDialectName() {
    return "No Suit Dialect";
  }
}
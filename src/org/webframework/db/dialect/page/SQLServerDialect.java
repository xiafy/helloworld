package org.webframework.db.dialect.page;

import org.webframework.Ps;

public class SQLServerDialect extends PageDialect
{
  public String getLimitString(String querySelect, int offset, int limit)
  {
    if (offset > 0) {
      throw new UnsupportedOperationException("数据库不支持带有偏移的分页查询");
    }
    return new StringBuffer(querySelect.length() + 8)
      .append(querySelect)
      .insert(getAfterSelectInsertPoint(querySelect), " top " + limit)
      .toString();
  }

  public void setLimitParam(Ps ps, boolean hasOffset, int offset, int limit)
  {
    if (hasOffset) {
      throw new UnsupportedOperationException("数据库不支持带有偏移的分页查询");
    }

    Ps psLimit = new Ps();

    int[] types = ps.getParamTypes();
    Object[] values = ps.getParams();
    for (int i = 0; i < types.length; i++) {
      psLimit.addParam(values[i], types[i]);
    }

    ps = psLimit;
  }

  static int getAfterSelectInsertPoint(String sql) {
    int selectIndex = sql.toLowerCase().indexOf("select");
    int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
    return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
  }

  public String getDialectName() {
    return "SQLServerDialect";
  }
}
package org.webframework.db.dialect.page;

import org.webframework.Ps;

public class OracleDialect extends Oracle9Dialect
{
  public String getLimitString(String sql, boolean hasOffset)
  {
    sql = sql.trim();
    boolean isForUpdate = false;
    if (sql.toLowerCase().endsWith(" for update")) {
      sql = sql.substring(0, sql.length() - 11);
      isForUpdate = true;
    }

    StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
    if (hasOffset) {
      pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
    }
    else {
      pagingSelect.append("select * from ( ");
    }
    pagingSelect.append(sql);
    if (hasOffset) {
      pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
    }
    else {
      pagingSelect.append(" ) where rownum <= ?");
    }

    if (isForUpdate) {
      pagingSelect.append(" for update");
    }

    return pagingSelect.toString();
  }

  public void setLimitParam(Ps ps, boolean hasOffset, int offset, int limit) {
    ps.addInt(limit);
    if (hasOffset)
      ps.addInt(offset);
  }

  public String getDialectName()
  {
    return "OracleDialect";
  }
}
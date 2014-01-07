package org.webframework.db.dialect.page;

import org.webframework.Ps;

public class DB2Dialect extends PageDialect
{
  public String getLimitString(String sql, boolean hasOffset)
  {
    int startOfSelect = sql.toLowerCase().indexOf("select");

    StringBuffer pagingSelect = new StringBuffer(sql.length() + 100)
      .append(sql.substring(0, startOfSelect))
      .append("select * from ( select ")
      .append(getRowNumber(sql));

    if (hasDistinct(sql)) {
      pagingSelect.append(" row_.* from ( ")
        .append(sql.substring(startOfSelect))
        .append(" ) as row_");
    }
    else {
      pagingSelect.append(sql.substring(startOfSelect + 6));
    }

    pagingSelect.append(" ) as temp_ where rownumber_ ");

    if (hasOffset) {
      pagingSelect.append(">=? and rownumber_ < ? ");
    }
    else {
      pagingSelect.append("<= ?");
    }

    return pagingSelect.toString();
  }

  public void setLimitParam(Ps ps, boolean hasOffset, int offset, int limit) {
    if (hasOffset) {
      ps.addInt(offset);
    }
    ps.addInt(limit);
  }

  private static boolean hasDistinct(String sql) {
    return sql.toLowerCase().indexOf("select distinct") >= 0;
  }

  private String getRowNumber(String sql) {
    StringBuffer rownumber = new StringBuffer(50)
      .append("rownumber() over(");

    int orderByIndex = sql.toLowerCase().indexOf("order by");

    if ((orderByIndex > 0) && (!hasDistinct(sql))) {
      rownumber.append(sql.substring(orderByIndex));
    }

    rownumber.append(") as rownumber_,");

    return rownumber.toString();
  }

  public String getDialectName() {
    return "DB2Dialect";
  }
}
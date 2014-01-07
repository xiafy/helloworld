package org.webframework.db.dialect.page;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.db.dialect.StandardDialectResolver;

public class PageDialectResolver
{
  private static final Log log = LogFactory.getLog(PageDialectResolver.class);

  private static PageDialectResolver instance = new PageDialectResolver();
  private Map pageDialects = new HashMap();

  public static PageDialectResolver getInstance()
  {
    return instance;
  }

  public PageDialect resolveDialectInternal(DataSource dataSource)
    throws SQLException
  {
    String databaseName = 
      StandardDialectResolver.getInstance().resolveDialectInternal(dataSource);
    PageDialect pageDialect = null;

    if (databaseName != null) {
      if (this.pageDialects.containsKey(databaseName)) {
        pageDialect = (PageDialect)this.pageDialects.get(databaseName);
      } else {
        try {
          pageDialect = resolveDialectInternal(databaseName);
        } catch (Exception e) {
          pageDialect = null;
        }
        this.pageDialects.put(databaseName, pageDialect);
      }
    }
    return pageDialect;
  }

  protected PageDialect resolveDialectInternal(String databaseName) throws SQLException {
    if (databaseName.indexOf("Oracle") == 0) {
      if ("Oracle8".equals(databaseName)) {
        return new Oracle8iDialect();
      }

      if ("Oracle9".equals(databaseName)) {
        return new Oracle9iDialect();
      }

      if ("Oracle10".equals(databaseName)) {
        return new Oracle10gDialect();
      }

      if ("Oracle11".equals(databaseName)) {
        log.warn("暂时不支持Oracle 11 ，使用Oracle 10的方言");
        return new Oracle10gDialect();
      }
      return new OracleDialect();
    }

    if (databaseName.indexOf("Microsoft SQL Server") == 0) {
      if ("Microsoft SQL Server2000".equals(databaseName)) {
        return new SQLServer2005Dialect();
      }

      if ("Microsoft SQL Server2005".equals(databaseName)) {
        return new SQLServer2005Dialect();
      }

      if ("Microsoft SQL Server2008".equals(databaseName)) {
        return new SQLServer2005Dialect();
      }
      return new SQLServerDialect();
    }

    if ("DB2".equals(databaseName)) {
      return new DB2Dialect();
    }

    if ("MySQL".equals(databaseName)) {
      return new MySQLDialect();
    }

    if ("HSQL Database Engine".equals(databaseName)) {
      return new HSQLDialect();
    }

    if ("PostgreSQL".equals(databaseName)) {
      return new PostgreSQLDialect();
    }

    if ("Apache Derby".equals(databaseName)) {
      return new DerbyDialect();
    }

    if ("Sybase SQL Server".equals(databaseName)) {
      return new SybaseDialect();
    }
    return null;
  }
}
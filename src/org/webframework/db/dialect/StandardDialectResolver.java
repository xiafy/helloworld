package org.webframework.db.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class StandardDialectResolver
{
  private static final Log log = LogFactory.getLog(StandardDialectResolver.class);
  public static final String HSQL = "HSQL Database Engine";
  public static final String MySQL = "MySQL";
  public static final String PostgreSQL = "PostgreSQL";
  public static final String ApacheDerby = "Apache Derby";
  public static final String SQLServer = "Microsoft SQL Server";
  public static final String SQLServer2000 = "Microsoft SQL Server2000";
  public static final String SQLServer2005 = "Microsoft SQL Server2005";
  public static final String SQLServer2008 = "Microsoft SQL Server2008";
  public static final String SybaseSQLServer = "Sybase SQL Server";
  public static final String DB2 = "DB2";
  public static final String Oracle = "Oracle";
  public static final String Oracle8 = "Oracle8";
  public static final String Oracle9 = "Oracle9";
  public static final String Oracle10 = "Oracle10";
  public static final String Oracle11 = "Oracle11";
  private static StandardDialectResolver instance = new StandardDialectResolver();
  private Map dataSourceDialects = new HashMap();

  public static StandardDialectResolver getInstance()
  {
    return instance;
  }

  public String resolveDialectInternal(DataSource dataSource)
  {
    String hashCode = String.valueOf(dataSource.hashCode());
    String databaseName = null;

    if (this.dataSourceDialects.containsKey(hashCode)) {
      databaseName = (String)this.dataSourceDialects.get(hashCode);
    } else {
      Connection con = null;
      try {
        con = DataSourceUtils.getConnection(dataSource);
        DatabaseMetaData dbmd = con.getMetaData();
        databaseName = resolveDialectInternal(dbmd);
      } catch (Exception e) {
        databaseName = null;

        if (con != null)
          try {
            con.close();
          }
          catch (SQLException e1) {
            e1.printStackTrace();
          }
      }
      finally
      {
        if (con != null) {
          try {
            con.close();
          }
          catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
      this.dataSourceDialects.put(hashCode, databaseName);
      log.info("初始化数据库方言：" + databaseName + ";dataSource Hash : " + dataSource.hashCode());
    }
    return databaseName;
  }

  protected String resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
    String databaseName = metaData.getDatabaseProductName();
    int databaseMajorVersion = metaData.getDatabaseMajorVersion();

    if ("HSQL Database Engine".equals(databaseName)) {
      return "HSQL Database Engine";
    }

    if ("MySQL".equals(databaseName)) {
      return "MySQL";
    }

    if ("PostgreSQL".equals(databaseName)) {
      return "PostgreSQL";
    }

    if ("Apache Derby".equals(databaseName)) {
      return "Apache Derby";
    }

    if (databaseName.startsWith("Microsoft SQL Server")) {
      switch (databaseMajorVersion) {
      case 8:
        return "Microsoft SQL Server2000";
      case 9:
        return "Microsoft SQL Server2005";
      case 10:
        return "Microsoft SQL Server2008";
      }
      log.warn("未识别的SQLServer版本：" + databaseMajorVersion);

      return "Microsoft SQL Server";
    }

    if (("Sybase SQL Server".equals(databaseName)) || ("Adaptive Server Enterprise".equals(databaseName))) {
      return "Sybase SQL Server";
    }

    if (databaseName.startsWith("DB2/")) {
      return "DB2";
    }

    if ("Oracle".equals(databaseName)) {
      switch (databaseMajorVersion) {
      case 11:
        return "Oracle11";
      case 10:
        return "Oracle10";
      case 9:
        return "Oracle9";
      case 8:
        return "Oracle8";
      }
      log.warn("未识别的Oracle版本：" + databaseMajorVersion);

      return "Oracle";
    }
    return null;
  }
}
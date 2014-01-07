package org.webframework.db.dialect.syntax;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.webframework.db.dialect.StandardDialectResolver;

public class SyntaxDialectResolver
{
  private static SyntaxDialectResolver instance = new SyntaxDialectResolver();
  private Map syntaxDialects = new HashMap();

  public static SyntaxDialectResolver getInstance()
  {
    return instance;
  }

  public SyntaxDialect resolveDialect(DataSource dataSource)
  {
    String databaseName = 
      StandardDialectResolver.getInstance().resolveDialectInternal(dataSource);
    SyntaxDialect syntaxDialect = null;

    if (databaseName != null) {
      if (this.syntaxDialects.containsKey(databaseName)) {
        syntaxDialect = (SyntaxDialect)this.syntaxDialects.get(databaseName);
      } else {
        try {
          syntaxDialect = resolveDialectInternal(databaseName);
        } catch (Exception e) {
          syntaxDialect = null;
        }
        this.syntaxDialects.put(databaseName, syntaxDialect);
      }
    }
    return syntaxDialect;
  }

  protected SyntaxDialect resolveDialectInternal(String databaseName) {
    if (databaseName.indexOf("Oracle") == 0) {
      return new OracleSyntaxDialect();
    }

    if (databaseName.indexOf("Microsoft SQL Server") == 0) {
      return new SQLServerSyntaxDialect();
    }

    if ("DB2".equals(databaseName)) {
      return null;
    }

    if ("MySQL".equals(databaseName)) {
      return null;
    }

    if ("HSQL Database Engine".equals(databaseName)) {
      return null;
    }

    if ("PostgreSQL".equals(databaseName)) {
      return null;
    }

    if ("Apache Derby".equals(databaseName)) {
      return null;
    }

    if ("Sybase SQL Server".equals(databaseName)) {
      return null;
    }
    return null;
  }
}
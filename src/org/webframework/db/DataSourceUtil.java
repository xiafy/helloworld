package org.webframework.db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DataSourceUtil
{
  public static void closeConnectionIfNecessary(Connection con, DataSource ds)
    throws Exception
  {
    if ((con == null) || (TransactionSynchronizationManager.hasResource(ds))) {
      return;
    }

    if ((!(ds instanceof SmartDataSource)) || (((SmartDataSource)ds).shouldClose(con)))
      try {
        con.close();
      }
      catch (SQLException ex) {
        throw new SQLException(ex);
      }
  }
}
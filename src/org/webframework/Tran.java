package org.webframework;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class Tran extends DefaultTransactionDefinition
{
  protected PlatformTransactionManager tm;
  protected TransactionStatus status = null;
  protected DataSource dataSource = null;

  public Tran()
    throws SQLException
  {
    this.dataSource = DB.getDataSource();
    this.tm = new DataSourceTransactionManager(this.dataSource);
  }

  public Tran(DataSource dataSource) {
    this.dataSource = dataSource;
    this.tm = new DataSourceTransactionManager(dataSource);
  }

  public Tran(DataSource dataSource, boolean isJtaTran)
  {
    if (isJtaTran) this.tm = new JtaTransactionManager(); else
      this.tm = new DataSourceTransactionManager(dataSource);
  }

  public Tran begin()
  {
    this.status = this.tm.getTransaction(this);
    return this;
  }

  public Tran commit()
  {
    this.tm.commit(this.status);
    return this;
  }

  public Tran rollback()
  {
    this.tm.rollback(this.status);
    return this;
  }

  public Connection getConnection()
  {
    return DataSourceUtils.getConnection(this.dataSource);
  }
}
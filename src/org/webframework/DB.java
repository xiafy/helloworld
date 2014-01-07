package org.webframework;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.webframework.db.WebDaoConfig;
import org.webframework.db.WebOper;
import org.webframework.db.WebQuery;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.db.dialect.syntax.SyntaxDialectResolver;
import org.webframework.exception.WebException;
import org.webframework.tag.grid.Grid;

public class DB
{
  public static DataSource getDataSource()
    throws SQLException
  {
    String dataSourceId = ((WebDaoConfig)BeanFactory.getBean("webDaoConfig")).getDefaultDataSourceId();
    return (DataSource)BeanFactory.getBean(dataSourceId);
  }

  public static DataSource getDataSource(String dataSource)
    throws SQLException
  {
    return (DataSource)BeanFactory.getBean(dataSource);
  }

  public static Connection getConnection()
    throws SQLException
  {
    return getDataSource().getConnection();
  }

  public static Connection getConnection(String dataSource)
    throws SQLException
  {
    return getDataSource(dataSource).getConnection();
  }

  public static Object get(String sql, Object bean)
    throws SQLException
  {
    return new WebQuery(sql).get(bean);
  }

  public static Object get(String dataSourceId, String sql, Object bean)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).get(bean);
  }

  public static Object getInOriginal(String sql, Object bean)
    throws SQLException
  {
    return new WebQuery(sql).getInOriginal(bean);
  }

  public static Object getInOriginal(String dataSourceId, String sql, Object bean)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getInOriginal(bean);
  }

  public static Object get(String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(sql).get(beanClass);
  }

  public static Object get(String dataSourceId, String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).get(beanClass);
  }

  public static Object getInOriginal(String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(sql).getInOriginal(beanClass);
  }

  public static Object getInOriginal(String dataSourceId, String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getInOriginal(beanClass);
  }

  public static Object get(String sql, Object bean, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).get(bean, ps);
  }

  public static Object get(String dataSourceId, String sql, Object bean, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).get(bean, ps);
  }

  public static Object getInOriginal(String sql, Object bean, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getInOriginal(bean, ps);
  }

  public static Object getInOriginal(String dataSourceId, String sql, Object bean, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getInOriginal(bean, ps);
  }

  public static Object get(String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).get(beanClass, ps);
  }

  public static Object get(String dataSourceId, String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).get(beanClass, ps);
  }

  public static Object getInOriginal(String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getInOriginal(beanClass, ps);
  }

  public static Object getInOriginal(String dataSourceId, String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getInOriginal(beanClass, ps);
  }

  public static WMap getMap(String sql)
    throws SQLException
  {
    return new WebQuery(sql).getMap();
  }

  public static WMap getMap(String dataSourceId, String sql)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMap();
  }

  public static WMap getMapInOriginal(String sql)
    throws SQLException
  {
    return new WebQuery(sql).getMapInOriginal();
  }

  public static WMap getMapInOriginal(String dataSourceId, String sql)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapInOriginal();
  }

  public static WMap getMap(String sql, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getMap(ps);
  }

  public static WMap getMap(String dataSourceId, String sql, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMap(ps);
  }

  public static WMap getMapInOriginal(String sql, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getMapInOriginal(ps);
  }

  public static Object get(String sql, Object bean, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).get(bean, new Ps(params));
  }

  public static Object getInOriginal(String sql, Object bean, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getInOriginal(bean, new Ps(params));
  }

  public static Object get(String sql, Class beanClass, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).get(beanClass, new Ps(params));
  }

  public static Object getInOriginal(String sql, Class beanClass, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getInOriginal(beanClass, new Ps(params));
  }

  public static Object getMap(String sql, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getMap(new Ps(params));
  }

  public static Object getMapInOriginal(String sql, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getMapInOriginal(new Ps(params));
  }

  public static List getList(String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(sql).getList(beanClass);
  }

  public static List getList(String dataSourceId, String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getList(beanClass);
  }

  public static List getListInOriginal(String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(sql).getListInOriginal(beanClass);
  }

  public static List getListInOriginal(String dataSourceId, String sql, Class beanClass)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getListInOriginal(beanClass);
  }

  public static List getList(String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getList(beanClass, ps);
  }

  public static List getList(String dataSourceId, String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getList(beanClass, ps);
  }

  public static List getListInOriginal(String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getListInOriginal(beanClass, ps);
  }

  public static List getListInOriginal(String dataSourceId, String sql, Class beanClass, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getListInOriginal(beanClass, ps);
  }

  public static List getList(String sql, Class beanClass, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getList(beanClass, offset, limit);
  }

  public static List getList(String dataSourceId, String sql, Class beanClass, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getList(beanClass, offset, limit);
  }

  public static List getListInOriginal(String sql, Class beanClass, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getListInOriginal(beanClass, offset, limit);
  }

  public static List getListInOriginal(String dataSourceId, String sql, Class beanClass, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getListInOriginal(beanClass, offset, limit);
  }

  public static List getList(String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getList(beanClass, ps, offset, limit);
  }

  public static List getList(String dataSourceId, String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getList(beanClass, ps, offset, limit);
  }

  public static List getListInOriginal(String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getListInOriginal(beanClass, ps, offset, limit);
  }

  public static List getListInOriginal(String dataSourceId, String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getListInOriginal(beanClass, ps, offset, limit);
  }

  public static List getMapList(String sql)
    throws SQLException
  {
    return new WebQuery(sql).getMapList();
  }

  public static List getMapList(String dataSourceId, String sql)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapList();
  }

  public static List getMapListInOriginal(String sql)
    throws SQLException
  {
    return new WebQuery(sql).getMapListInOriginal();
  }

  public static List getMapListInOriginal(String dataSourceId, String sql)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapListInOriginal();
  }

  public static List getMapList(String sql, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getMapList(ps);
  }

  public static List getMapList(String dataSourceId, String sql, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapList(ps);
  }

  public static List getMapListInOriginal(String sql, Ps ps)
    throws SQLException
  {
    return new WebQuery(sql).getMapListInOriginal(ps);
  }

  public static List getMapListInOriginal(String dataSourceId, String sql, Ps ps)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapListInOriginal(ps);
  }

  public static List getMapList(String sql, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getMapList(offset, limit);
  }

  public static List getMapList(String dataSourceId, String sql, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapList(offset, limit);
  }

  public static List getMapListInOriginal(String sql, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getMapListInOriginal(offset, limit);
  }

  public static List getMapListInOriginal(String dataSourceId, String sql, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapListInOriginal(offset, limit);
  }

  public static List getMapList(String sql, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getMapList(ps, offset, limit);
  }

  public static List getMapList(String dataSourceId, String sql, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId).getMapList(ps, offset, limit);
  }

  public static List getMapListInOriginal(String sql, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getMapListInOriginal(ps, offset, limit);
  }

  public static List getMapListInOriginal(String dataSourceId, String sql, Ps ps, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(dataSourceId, sql).getMapListInOriginal(ps, offset, limit);
  }

  public static List getList(String sql, Class beanClass, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getList(beanClass, new Ps(params));
  }

  public static List getListInOriginal(String sql, Class beanClass, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getListInOriginal(beanClass, new Ps(params));
  }

  public static List getList(String sql, Class beanClass, Object[] params, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getList(beanClass, new Ps(params), offset, limit);
  }

  public static List getListInOriginal(String sql, Class beanClass, Object[] params, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getListInOriginal(beanClass, new Ps(params), offset, limit);
  }

  public static List getMapList(String sql, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getMapList(new Ps(params));
  }

  public static List getMapListInOriginal(String sql, Object[] params)
    throws SQLException
  {
    return new WebQuery(sql).getMapListInOriginal(new Ps(params));
  }

  public static List getMapList(String sql, Object[] params, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getMapList(new Ps(params), offset, limit);
  }

  public static List getMapListInOriginal(String sql, Object[] params, int offset, int limit)
    throws SQLException
  {
    return new WebQuery(sql).getMapListInOriginal(new Ps(params), offset, limit);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass);
  }

  public static Grid getGridList(Grid grid, String dataSourceId, String sql, Class beanClass)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridList(grid, beanClass);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass);
  }

  public static Grid getGridListInOriginal(Grid grid, String dataSourceId, String sql, Class beanClass)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridListInOriginal(grid, beanClass);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, ps);
  }

  public static Grid getGridList(Grid grid, String dataSourceId, String sql, Class beanClass, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridList(grid, beanClass, ps);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, ps);
  }

  public static Grid getGridListInOriginal(Grid grid, String dataSourceId, String sql, Class beanClass, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridListInOriginal(grid, beanClass, ps);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, limit);
  }

  public static Grid getGridList(Grid grid, String dataSourceId, String sql, Class beanClass, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridList(grid, beanClass, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String dataSourceId, String sql, Class beanClass, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridListInOriginal(grid, beanClass, limit);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, offset, limit);
  }

  public static Grid getGridList(Grid grid, String dataSourceId, String sql, Class beanClass, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridList(grid, beanClass, offset, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, offset, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String dataSourceId, String sql, Class beanClass, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridListInOriginal(grid, beanClass, offset, limit);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, ps, limit);
  }

  public static Grid getGridList(Grid grid, String dataSourceId, String sql, Class beanClass, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridList(grid, beanClass, ps, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, ps, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String dataSourceId, String sql, Class beanClass, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridListInOriginal(grid, beanClass, ps, limit);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, ps, offset, limit);
  }

  public static Grid getGridList(Grid grid, String dataSourceId, String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridList(grid, beanClass, ps, offset, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, ps, offset, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String dataSourceId, String sql, Class beanClass, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridListInOriginal(grid, beanClass, ps, offset, limit);
  }

  public static Grid getGridMapList(Grid grid, String sql)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapList(grid);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid);
  }

  public static Grid getGridMapList(Grid grid, String sql, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, ps);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapList(grid, ps);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, ps);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql, Ps ps)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid, ps);
  }

  public static Grid getGridMapList(Grid grid, String sql, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, limit);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapList(grid, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid, limit);
  }

  public static Grid getGridMapList(Grid grid, String sql, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, offset, limit);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapList(grid, offset, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, offset, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid, offset, limit);
  }

  public static Grid getGridMapList(Grid grid, String sql, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, ps, limit);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId).getGridMapList(grid, ps, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, ps, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql, Ps ps, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid, ps, limit);
  }

  public static Grid getGridMapList(Grid grid, String sql, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, ps, offset, limit);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId).getGridMapList(grid, ps, offset, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, ps, offset, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql, Ps ps, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid, ps, offset, limit);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, Object[] params)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, new Ps(params));
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, Object[] params)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, new Ps(params));
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, Object[] params, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, new Ps(params), limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, Object[] params, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, new Ps(params), limit);
  }

  public static Grid getGridList(Grid grid, String sql, Class beanClass, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridList(grid, beanClass, new Ps(params), offset, limit);
  }

  public static Grid getGridList(Grid grid, String dataSourceId, String sql, Class beanClass, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridList(grid, beanClass, new Ps(params), offset, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String sql, Class beanClass, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridListInOriginal(grid, beanClass, new Ps(params), offset, limit);
  }

  public static Grid getGridListInOriginal(Grid grid, String dataSourceId, String sql, Class beanClass, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridListInOriginal(grid, beanClass, new Ps(params), offset, limit);
  }

  public static Grid getMapList(Grid grid, String sql, Object[] params)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, new Ps(params));
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, Object[] params)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, new Ps(params));
  }

  public static Grid getGridMapList(Grid grid, String sql, Object[] params, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, new Ps(params), limit);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql, Object[] params, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapList(grid, new Ps(params), limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, Object[] params, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, new Ps(params), limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql, Object[] params, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid, new Ps(params), limit);
  }

  public static Grid getGridMapList(Grid grid, String sql, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapList(grid, new Ps(params), offset, limit);
  }

  public static Grid getGridMapList(Grid grid, String dataSourceId, String sql, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapList(grid, new Ps(params), offset, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String sql, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(sql).getGridMapListInOriginal(grid, new Ps(params), offset, limit);
  }

  public static Grid getGridMapListInOriginal(Grid grid, String dataSourceId, String sql, Object[] params, int offset, int limit)
    throws SQLException, WebException
  {
    return new WebQuery(dataSourceId, sql).getGridMapListInOriginal(grid, new Ps(params), offset, limit);
  }

  public static int update(String sql)
    throws SQLException
  {
    return new WebOper(sql).update();
  }

  public static int update(String dataSourceId, String sql)
    throws SQLException
  {
    return new WebOper(dataSourceId, sql).update();
  }

  public static int update(String sql, Ps ps)
    throws SQLException
  {
    return new WebOper(sql).update(ps);
  }

  public static int update(String dataSourceId, String sql, Ps ps)
    throws SQLException
  {
    return new WebOper(dataSourceId, sql).update(ps);
  }

  public static int[] batchUpdate(String[] sql)
    throws SQLException
  {
    return new WebOper(null).batchUpdate(sql);
  }

  public static int[] batchUpdate(String dataSourceId, String[] sql)
    throws SQLException
  {
    return new WebOper(dataSourceId, null).batchUpdate(sql);
  }

  public static int[] batchUpdate(String sql, Ps[] ps)
    throws SQLException
  {
    return new WebOper(sql).batchUpdate(ps);
  }

  public static int[] batchUpdate(String dataSourceId, String sql, Ps[] ps)
    throws SQLException
  {
    return new WebOper(dataSourceId, sql).batchUpdate(ps);
  }

  public int update(String sql, Object[] params)
  {
    return -1;
  }
  public int[] batchUpdate(String sql, Object[][] params) {
    return null;
  }

  public static Tran beginTransaction()
    throws SQLException
  {
    return new Tran();
  }

  public static Tran beginTransaction(String dataSourceId)
    throws SQLException
  {
    return new Tran(getDataSource(dataSourceId));
  }

  public static SyntaxDialect getSyntaxDialect(DataSource dataSource)
  {
    return SyntaxDialectResolver.getInstance().resolveDialect(dataSource);
  }
  public static SyntaxDialect getSyntaxDialect(String dataSource) {
    try {
      return getSyntaxDialect(getDataSource(dataSource));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
  public static SyntaxDialect getSyntaxDialect() {
    try {
      return getSyntaxDialect(getDataSource());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
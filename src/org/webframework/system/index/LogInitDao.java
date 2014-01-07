package org.webframework.system.index;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.Tran;
import org.webframework.WMap;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.system.log.track.LogUtils;

public class LogInitDao
{
  public List getTreeNode(String gnmkDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    Ps ps = new Ps();
    if ((gnmkDm == null) || ("".equals(gnmkDm))) {
      sql.append("SELECT ");
      sql.append("T.GNMK_DM ID,T.GNMK_MC NAME,T.SJ_GNMK_DM P_ID,T.XH SEQ,T.TYPE ");
      sql.append("FROM ");
      sql.append("QX_GNMK T ");
      sql.append("WHERE ");
      sql.append("T.SJ_GNMK_DM IS NULL ");
      sql.append("ORDER BY T.XH ASC ");
    } else {
      sql.append("SELECT ");
      sql.append("T.GNMK_DM ID,T.GNMK_MC NAME,T.SJ_GNMK_DM P_ID,T.XH SEQ,T.TYPE ");
      sql.append("FROM ");
      sql.append("QX_GNMK T ");
      sql.append("WHERE ");
      sql.append("T.SJ_GNMK_DM=? ");
      sql.append("ORDER BY T.XH ASC ");
      ps.addString(gnmkDm);
    }
    return DB.getMapList(sql.toString(), ps);
  }

  public List getResNode(String gnmkDm) throws Exception {
    SyntaxDialect dialect = DB.getSyntaxDialect();
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.ZY_DM ID,T.ZY_MC NAME,T.GNMK_DM P_ID ");
    sql.append("FROM ");
    sql.append("QX_GNMK_ZY T ");
    sql.append("WHERE ");
    sql.append("T.GNMK_DM=? ");
    sql.append("ORDER BY ").append(dialect.string2Number(dialect.SUBSTRING("ZY_DM", 2))).append(" ASC ");

    Ps ps = new Ps();
    ps.addString(gnmkDm);
    return DB.getMapList(sql.toString(), ps);
  }

  public List getConfigList(String pId) throws Exception {
    StringBuffer sql = new StringBuffer();
    Ps ps = new Ps();
    if ("".equals(pId)) {
      sql.append("SELECT ");
      sql.append("T.ID,T.BASE,T.REQUEST,T.COOKIE,T.SQL,T.EXCEPTION ");
      sql.append("FROM ");
      sql.append("W11_INIT_LOG T,QX_GNMK T1 ");
      sql.append("WHERE ");
      sql.append("T.ID=T1.GNMK_DM ");
      sql.append("AND T1.SJ_GNMK_DM IS NULL ");
    } else {
      sql.append("SELECT ");
      sql.append("T.ID,T.BASE,T.REQUEST,T.COOKIE,T.SQL,T.EXCEPTION ");
      sql.append("FROM ");
      sql.append("W11_INIT_LOG T,QX_GNMK T1 ");
      sql.append("WHERE ");
      sql.append("T.ID=T1.GNMK_DM ");
      sql.append("AND T1.SJ_GNMK_DM=? ");
      sql.append("UNION ALL ");
      sql.append("SELECT ");
      sql.append("T.ID,T.BASE,T.REQUEST,T.COOKIE,T.SQL,T.EXCEPTION ");
      sql.append("FROM ");
      sql.append("W11_INIT_LOG T,QX_GNMK_ZY T2 ");
      sql.append("WHERE ");
      sql.append("T.ID=T2.ZY_DM ");
      sql.append("AND T2.GNMK_DM=? ");

      ps.addString(pId);
      ps.addString(pId);
    }
    return DB.getMapList(sql.toString(), ps);
  }

  public WMap getDefaultParams() throws Exception {
    WMap view = new WMap();
    String[] key = { "base", "request", "cookie", "sql", "exception", "login", "interval" };
    List keyList = Arrays.asList(new String[] { "log_base", "log_request", "log_cookie", "log_sql", "log_exception", "log_login", "log_write_interval" });

    int len = key.length;
    StringBuffer scope = new StringBuffer();
    for (int i = 0; i < len; i++) {
      view.put(key[i], "0");
      scope.append("'").append(keyList.get(i).toString()).append("'");
      if (i + 1 != len) {
        scope.append(",");
      }
    }
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT PARAM_ID, PARAM_VALUE ");
    sql.append("  FROM SYS_CS T ");
    sql.append(" WHERE T.PARAM_ID IN (").append(scope.toString()).append(")");

    List list = DB.getMapList(sql.toString());
    len = list.size();
    WMap map = null;
    for (int i = 0; i < len; i++) {
      map = (WMap)list.get(i);
      view.put(key[keyList.indexOf(map.getString("paramId"))], map.getString("paramValue"));
    }
    return view;
  }

  public void saveLog(WMap view, List logList) throws Exception {
    int len = logList.size();
    Ps[] deletePs = new Ps[len - 1];
    List insertPsList = new ArrayList();

    WMap map = null;

    for (int i = 0; i < len; i++) {
      map = (WMap)logList.get(i);
      String id = map.getString("id");
      String base = map.getString("base");
      String request = map.getString("request");
      String cookie = map.getString("cookie");
      String sqlConfig = map.getString("sql");
      String exception = map.getString("exception");

      if (i != 0) {
        deletePs[(i - 1)] = new Ps();
        deletePs[(i - 1)].addString(id);

        if ((!"1".equals(base)) && (!"1".equals(request)) && (!"1".equals(cookie)) && 
          (!"1".equals(sqlConfig)) && (!"1".equals(exception))) continue;
        Ps ps = new Ps();
        ps.addString(id);
        ps.addString(base);
        ps.addString(request);
        ps.addString(cookie);
        ps.addString(sqlConfig);
        ps.addString(exception);
        insertPsList.add(ps);
      }
    }

    StringBuffer sql = new StringBuffer();
    Tran tran = new Tran().begin();
    try
    {
      saveBaseConfig(view);

      if (deletePs.length > 0) {
        sql.setLength(0);
        sql.append("DELETE FROM W11_INIT_LOG WHERE ID=? ");
        DB.batchUpdate(sql.toString(), deletePs);
      }

      if (!insertPsList.isEmpty()) {
        sql.setLength(0);
        sql.append("INSERT INTO W11_INIT_LOG(ID,BASE,REQUEST,COOKIE,SQL,EXCEPTION) VALUES (?,?,?,?,?,?) ");
        DB.batchUpdate(sql.toString(), (Ps[])insertPsList.toArray(new Ps[insertPsList.size()]));
      }
      tran.commit();
    } catch (Exception e) {
      tran.rollback();
      throw e;
    }
    LogUtils.refreshResLogConfig();
  }

  private void saveBaseConfig(WMap view) throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.setLength(0);
    sql.append("UPDATE SYS_CS SET PARAM_VALUE=? WHERE PARAM_ID='log_write_interval'");
    Ps ps = new Ps();
    ps.addString(view.getString("interval"));
    DB.update(sql.toString(), ps);

    sql.setLength(0);
    sql.append("UPDATE SYS_CS SET PARAM_VALUE=? WHERE PARAM_ID='log_base'");
    ps = new Ps();
    ps.addString(view.getString("base"));
    DB.update(sql.toString(), ps);

    sql.setLength(0);
    sql.append("UPDATE SYS_CS SET PARAM_VALUE=? WHERE PARAM_ID='log_login'");
    ps = new Ps();
    ps.addString(view.getString("login"));
    DB.update(sql.toString(), ps);

    sql.setLength(0);
    sql.append("UPDATE SYS_CS SET PARAM_VALUE=? WHERE PARAM_ID='log_request'");
    ps = new Ps();
    ps.addString(view.getString("request"));
    DB.update(sql.toString(), ps);

    sql.setLength(0);
    sql.append("UPDATE SYS_CS SET PARAM_VALUE=? WHERE PARAM_ID='log_cookie'");
    ps = new Ps();
    ps.addString(view.getString("cookie"));
    DB.update(sql.toString(), ps);

    sql.setLength(0);
    sql.append("UPDATE SYS_CS SET PARAM_VALUE=? WHERE PARAM_ID='log_sql'");
    ps = new Ps();
    ps.addString(view.getString("sql"));
    DB.update(sql.toString(), ps);

    sql.setLength(0);
    sql.append("UPDATE SYS_CS SET PARAM_VALUE=? WHERE PARAM_ID='log_exception'");
    ps = new Ps();
    ps.addString(view.getString("exception"));
    DB.update(sql.toString(), ps);
  }
}
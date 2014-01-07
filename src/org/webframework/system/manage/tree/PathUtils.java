package org.webframework.system.manage.tree;

import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.system.manage.entries.PathTable;

public class PathUtils
{
  public static String getMyPathByPId(PathTable table)
    throws Exception
  {
    String tableName = table.getTableName();
    String idField = table.getIdField();
    String pathField = table.getPathField();
    String pId = table.getPId();
    String id = table.getId();

    String path = "";
    if ((pId != null) && (!"".equals(pId))) {
      StringBuffer sql = new StringBuffer();
      sql.append(" SELECT ");
      sql.append(pathField + " PATH ");
      sql.append(" FROM ");
      sql.append(tableName);
      sql.append(" WHERE ");
      sql.append(idField);
      sql.append("=? ");
      Ps ps = new Ps();
      ps.addString(pId);
      path = DB.getMap(sql.toString(), ps).getString("path");
    }
    return path + id + "#";
  }

  public static String getMyPathByPId(String tableName, String idField, String pathField, String pId, String id) throws Exception {
    PathTable table = new PathTable().setTableName(tableName)
      .setIdField(idField).setPathField(pathField)
      .setPId(pId).setId(id);
    return getMyPathByPId(table);
  }
  public static String getMyPathByPId(String tableName, String pId, String id) throws Exception {
    PathTable table = new PathTable().setTableName(tableName)
      .setPId(pId).setId(id);
    return getMyPathByPId(table);
  }

  public static String getWholePathNameById(PathTable table)
    throws Exception
  {
    String tableName = table.getTableName();
    String pIdField = table.getPIdField();
    String idField = table.getIdField();
    String nameField = table.getNameField();
    String id = table.getId();

    StringBuffer result = new StringBuffer();
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT ");
    sql.append(nameField + " NAME," + pIdField + " P_ID ");
    sql.append(" FROM ");
    sql.append(tableName);
    sql.append(" WHERE ");
    sql.append(idField + "= ? ");
    Ps ps = null;
    WMap wmap = null;

    while ((id != null) && (!"".equals(id))) {
      ps = new Ps();
      ps.addString(id);
      wmap = DB.getMap(sql.toString(), ps);
      result.insert(0, "/" + wmap.getString("name"));
      id = wmap.getString("pId");
    }

    return result.toString();
  }

  public static String getWholePathNameById(String tableName, String pIdField, String idField, String nameField, String pId, String id) throws Exception {
    PathTable table = new PathTable().setTableName(tableName)
      .setPIdField(pIdField).setIdField(idField).setNameField(nameField)
      .setPId(pId).setId(id);
    return getWholePathNameById(table);
  }
  public static String getWholePathNameById(String tableName, String pId, String id) throws Exception {
    PathTable table = new PathTable().setTableName(tableName)
      .setPId(pId).setId(id);
    return getWholePathNameById(table);
  }

  public static void refreshPathById(PathTable table)
    throws Exception
  {
    SyntaxDialect dialect = DB.getSyntaxDialect();
    String CONCAT = dialect.CONCAT();
    String tableName = table.getTableName();
    String pIdField = table.getPIdField();
    String idField = table.getIdField();
    String pathField = table.getPathField();
    String id = table.getId();

    StringBuffer sql = new StringBuffer();

    sql.append(" UPDATE " + tableName);
    sql.append(" SET ");
    sql.append(pathField + "=NULL ");
    sql.append(" WHERE ");
    sql.append(pathField + " LIKE (SELECT " + pathField + " FROM " + tableName + " WHERE " + idField + "=?)" + CONCAT + "'%'");
    Ps ps = new Ps();
    ps.addString(id);
    DB.update(sql.toString(), ps);

    sql.setLength(0);
    sql.append("  UPDATE " + tableName);
    sql.append("  SET " + pathField + "=" + idField + CONCAT + "'#' ");
    sql.append("  WHERE ");
    sql.append(pIdField + " IS NULL");
    DB.update(sql.toString());

    sql.setLength(0);
    sql.append(dialect.getUpdateSQLWithAlias(tableName, "T"));
    sql.append("    SET T." + pathField + " = (SELECT T1." + pathField + CONCAT + "T." + idField + CONCAT + " '#'");
    sql.append("                         \tFROM " + tableName + " T1");
    sql.append("                        \tWHERE T." + pIdField + "=T1." + idField + ")");
    sql.append("  WHERE T." + pathField + " IS NULL");
    sql.append("    AND EXISTS (SELECT T2." + pathField);
    sql.append("           FROM " + tableName + " T2");
    sql.append("          WHERE T." + pIdField + " = T2." + idField);
    sql.append("            AND T2." + pathField + " IS NOT NULL)");
    int num = DB.update(sql.toString());
    while (num != 0)
      num = DB.update(sql.toString());
  }

  public static void refreshPathById(String tableName, String pIdField, String idField, String pathField, String id) throws Exception
  {
    PathTable table = new PathTable().setTableName(tableName)
      .setPIdField(pIdField).setIdField(idField).setPathField(pathField)
      .setId(id);
    refreshPathById(table);
  }
  public static void refreshPathById(String tableName, String id) throws Exception {
    PathTable table = new PathTable().setTableName(tableName).setId(id);
    refreshPathById(table);
  }

  public static String getLowerLevelById(PathTable table, String zdFiled)
    throws Exception
  {
    String tableName = table.getTableName();
    String idField = table.getIdField();
    String pathField = table.getPathField();
    String id = table.getId();

    StringBuffer sql = new StringBuffer();
    sql.append(" EXISTS(");
    sql.append("       SELECT 1 FROM (");
    sql.append("         SELECT W11_M1." + idField);
    sql.append("         FROM " + tableName + " W11_M1");
    sql.append("         WHERE EXISTS (SELECT W11_M2." + pathField);
    sql.append("               FROM " + tableName + " W11_M2");
    sql.append("              WHERE W11_M2." + idField + " = ").append(id);
    sql.append("                AND W11_M1." + pathField + " LIKE W11_M2." + pathField + DB.getSyntaxDialect().CONCAT() + "'%')");
    sql.append("       ) W11_M3");
    sql.append("       WHERE W11_M3." + idField + "=").append(zdFiled);
    sql.append("     ) ");
    return sql.toString();
  }

  public static String getLowerLevelById(String tableName, String idField, String pathField, String id, String zdFiled) throws Exception {
    PathTable table = new PathTable().setTableName(tableName)
      .setIdField(idField).setPathField(pathField)
      .setId(id);
    return getLowerLevelById(table, zdFiled);
  }
  public static String getLowerLevelById(String tableName, String id, String zdFiled) throws Exception {
    PathTable table = new PathTable().setTableName(tableName).setId(id);
    return getLowerLevelById(table, zdFiled);
  }

  public static void refreshTablePath(PathTable table)
    throws Exception
  {
    SyntaxDialect dialect = DB.getSyntaxDialect();
    String CONCAT = dialect.CONCAT();
    String tableName = table.getTableName();
    String idField = table.getIdField();
    String pIdField = table.getPIdField();
    String pathField = table.getPathField();

    StringBuffer sql = new StringBuffer();

    sql.append("UPDATE " + tableName + " SET " + pathField + "=NULL ");
    DB.update(sql.toString());

    sql.setLength(0);
    sql.append("  UPDATE " + tableName);
    sql.append("  SET " + pathField + "=" + idField + CONCAT + "'#' ");
    sql.append("  WHERE ");
    sql.append(pIdField + " IS NULL");
    DB.update(sql.toString());

    sql.setLength(0);
    sql.append(dialect.getUpdateSQLWithAlias(tableName, "T"));
    sql.append("    SET T." + pathField + " = (SELECT T1." + pathField + CONCAT + "T." + idField + CONCAT + "'#'");
    sql.append("                         \tFROM " + tableName + " T1");
    sql.append("                        \tWHERE T." + pIdField + "=T1." + idField + ")");
    sql.append("  WHERE T." + pathField + " IS NULL");
    sql.append("    AND EXISTS (SELECT T2." + pathField);
    sql.append("           FROM " + tableName + " T2");
    sql.append("          WHERE T." + pIdField + " = T2." + idField);
    sql.append("            AND T2." + pathField + " IS NOT NULL)");
    int num = DB.update(sql.toString());
    while (num != 0)
      num = DB.update(sql.toString());
  }

  public static void refreshTablePath(String tableName, String pIdField, String idField, String pathField) throws Exception
  {
    PathTable table = new PathTable().setTableName(tableName)
      .setPIdField(pIdField).setIdField(idField).setPathField(pathField);
    refreshTablePath(table);
  }
  public static void refreshTablePath(String tableName) throws Exception {
    PathTable table = new PathTable().setTableName(tableName);
    refreshTablePath(table);
  }

  public static void refreshTable()
    throws Exception
  {
    refreshTablePath("QX_GNMK");
  }
}
package org.webframework.system.index;

import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.db.dialect.syntax.SyntaxDialect;

public class IndexDao
{
  public WMap getUserLastLoginLog(String czryDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT MAX(T.LOG_ID) LOG_ID ");
    sql.append("  FROM LOG_LOGIN T ");
    sql.append(" WHERE T.CZRY_DM = ? ");
    sql.append("   AND T.TYPE = '1' ");
    Ps ps = new Ps();
    ps.addString(czryDm);
    WMap map = DB.getMap(sql.toString(), ps);
    if (map != null) {
      String logId = map.getString("logId");
      sql.setLength(0);
      sql.append("SELECT ").append(DB.getSyntaxDialect().dateTime2String("T.TIME")).append(" TIME, T.USER_IP ");
      sql.append("  FROM LOG_LOGIN T ");
      sql.append(" WHERE T.LOG_ID = (SELECT MAX(T1.LOG_ID) ");
      sql.append("                     FROM LOG_LOGIN T1 ");
      sql.append("                    WHERE T1.CZRY_DM = ? ");
      sql.append("                      AND T1.TYPE = '1' ");
      sql.append("                      AND T1.LOG_ID < ?) ");
      ps = new Ps();
      ps.addString(czryDm);
      ps.addString(logId);
      map = DB.getMap(sql.toString(), ps);
      return map;
    }
    return null;
  }

  public WMap getUserAvatar(String avatarId) throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT T.IMG_PATH, T.WIDTH, T.HEIGHT ");
    sql.append("  FROM SYS_CZRY_CS T ");
    sql.append(" WHERE T.AVATAR_ID = ? ");
    Ps ps = new Ps();
    ps.addString(avatarId);
    return DB.getMap(sql.toString(), ps);
  }
}
package org.webframework.system.plugin.cp;

import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.webframework.BeanFactory;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.Tran;
import org.webframework.WMap;
import org.webframework.system.SysConfig;
import org.webframework.system.SysContext;
import org.webframework.system.login.auth.algorithm.IEncrypt;

public class ControlPanelDao
{
  public WMap getUserParams(String czryDm)
    throws SQLException
  {
    String sql = "SELECT T.PARAM_ID,T.PARAM_VALUE FROM SYS_CZRY T WHERE T.CZRY_DM=?";
    Ps ps = new Ps();
    ps.setString(1, czryDm);
    List list = DB.getMapList(sql, ps);

    WMap params = new WMap();
    for (int i = 0; i < list.size(); i++) {
      WMap m = (WMap)list.get(i);
      params.set(m.getString("paramId"), m.getString("paramValue"));
    }

    return params;
  }

  public List getAvatars()
    throws SQLException
  {
    String sql = "SELECT * FROM SYS_CZRY_CS T ORDER BY T.AVATAR_ID";
    return DB.getMapList(sql);
  }

  public WMap getAvatar(String avatarId)
    throws SQLException
  {
    String sql = "SELECT * FROM SYS_CZRY_CS T WHERE T.AVATAR_ID=?";
    Ps ps = new Ps();
    ps.setString(1, avatarId);
    return DB.getMap(sql, ps);
  }

  public String getDefaultAvatar()
    throws SQLException
  {
    String sql = "SELECT T.PARAM_VALUE FROM SYS_CS T WHERE T.PARAM_ID = ?";
    Ps ps = new Ps();
    ps.setString(1, "user_avatar");
    WMap m = DB.getMap(sql, ps);
    if (m == null) return null;
    return m.getString("paramValue");
  }

  public List getThemes()
    throws SQLException
  {
    String sql = "SELECT * FROM SYS_XTZT T";

    return null;
  }

  public String getPassword(String czryDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.PASSWORD ");
    sql.append("FROM ");
    sql.append("QX_CZRY_ZHXX T ");
    sql.append("WHERE ");
    sql.append("T.CZRY_DM=? ");
    Ps ps = new Ps();
    ps.addString(czryDm.toUpperCase());
    return DB.getMap(sql.toString(), ps).getString("password");
  }

  public void resetUserFont(HttpServletRequest request, String czryDm, String fontSize, String fontFamily)
    throws Exception
  {
    String sqlDelete = "DELETE FROM SYS_CZRY WHERE CZRY_DM=? AND PARAM_ID=?";
    String sqlInsert = "INSERT INTO SYS_CZRY(CZRY_DM,PARAM_ID,PARAM_VALUE) VALUES(?,?,?)";

    Ps psDeleteSize = new Ps();
    psDeleteSize.setString(1, czryDm);
    psDeleteSize.setString(2, "style_font_size");

    Ps psDeleteFamily = new Ps();
    psDeleteFamily.setString(1, czryDm);
    psDeleteFamily.setString(2, "style_font_family");

    Ps psInsertSize = new Ps();
    psInsertSize.setString(1, czryDm);
    psInsertSize.setString(2, "style_font_size");
    psInsertSize.setString(3, fontSize);

    Ps psInsertFamily = new Ps();
    psInsertFamily.setString(1, czryDm);
    psInsertFamily.setString(2, "style_font_family");
    psInsertFamily.setString(3, fontFamily);

    Tran tran = new Tran().begin();
    try {
      DB.update(sqlDelete, psDeleteSize);
      DB.update(sqlDelete, psDeleteFamily);

      DB.update(sqlInsert, psInsertSize);
      DB.update(sqlInsert, psInsertFamily);

      tran.commit();
    } catch (Exception e) {
      tran.rollback();
      throw e;
    }

    SysContext.refreshUserConfig(request.getSession());
  }

  public void resetUserTheme(HttpServletRequest request, String czryDm, String theme)
    throws Exception
  {
    String sqlDelete = "DELETE FROM SYS_CZRY WHERE CZRY_DM=? AND PARAM_ID=?";
    String sqlInsert = "INSERT INTO SYS_CZRY(CZRY_DM,PARAM_ID,PARAM_VALUE) VALUES(?,?,?)";

    Ps psDeleteTheme = new Ps();
    psDeleteTheme.setString(1, czryDm);
    psDeleteTheme.setString(2, "style_theme");

    Ps psInsertTheme = new Ps();
    psInsertTheme.setString(1, czryDm);
    psInsertTheme.setString(2, "style_theme");
    psInsertTheme.setString(3, theme);

    Tran tran = new Tran().begin();
    try {
      DB.update(sqlDelete, psDeleteTheme);

      DB.update(sqlInsert, psInsertTheme);

      tran.commit();
    } catch (Exception e) {
      tran.rollback();
      throw e;
    }

    SysContext.refreshUserConfig(request.getSession());
  }

  public void resetUserAvatar(HttpServletRequest request, String czryDm, String avatarId)
    throws Exception
  {
    String sqlDelete = "DELETE FROM SYS_CZRY WHERE CZRY_DM=? AND PARAM_ID=?";
    String sqlInsert = "INSERT INTO SYS_CZRY(CZRY_DM,PARAM_ID,PARAM_VALUE) VALUES(?,?,?)";

    Ps psDeleteAvatar = new Ps();
    psDeleteAvatar.setString(1, czryDm);
    psDeleteAvatar.setString(2, "user_avatar");

    Ps psInsertAvatar = new Ps();
    psInsertAvatar.setString(1, czryDm);
    psInsertAvatar.setString(2, "user_avatar");
    psInsertAvatar.setString(3, avatarId);

    Tran tran = new Tran().begin();
    try {
      DB.update(sqlDelete, psDeleteAvatar);
      DB.update(sqlInsert, psInsertAvatar);

      tran.commit();
    } catch (Exception e) {
      tran.rollback();
      throw e;
    }

    SysContext.refreshUserConfig(request.getSession());
  }

  public void saveAll(String czryDm, WMap view)
    throws Exception
  {
    StringBuffer sqlUpdateAclUser = new StringBuffer();
    sqlUpdateAclUser.append("UPDATE QX_CZRY SET ");
    sqlUpdateAclUser.append("SFZHM=?,DHHM=?,SJHM=?,DYDZ=?,DZ=?,MSN=?,QQ=?,BZ=? ");
    sqlUpdateAclUser.append("WHERE CZRY_DM=? ");

    String sqlUpdatePassword = "UPDATE QX_CZRY_ZHXX SET PASSWORD=? WHERE CZRY_DM=?";

    Ps psUpdateAclUser = new Ps();
    psUpdateAclUser.addString(view.getString("sfzhm"));
    psUpdateAclUser.addString(view.getString("dhhm"));
    psUpdateAclUser.addString(view.getString("sjhm"));
    psUpdateAclUser.addString(view.getString("dydz"));
    psUpdateAclUser.addString(view.getString("dz"));
    psUpdateAclUser.addString(view.getString("msn"));
    psUpdateAclUser.addString(view.getString("qq"));
    psUpdateAclUser.addString(view.getString("bz"));
    psUpdateAclUser.addString(czryDm);

    Tran tran = new Tran().begin();
    try
    {
      DB.update(sqlUpdateAclUser.toString(), psUpdateAclUser);

      String newPassword = view.getString("newPassword");
      if ((newPassword != null) && (!"".equals(newPassword))) {
        IEncrypt passwordEncrypt = ((SysConfig)BeanFactory.getBean("sysConfig")).getPasswordEncrypt();
        Ps psUpdatePassword = new Ps();
        psUpdatePassword.addString(passwordEncrypt.encode(newPassword));
        psUpdatePassword.addString(czryDm);
        DB.update(sqlUpdatePassword, psUpdatePassword);
      }
      tran.commit();
    } catch (Exception e) {
      tran.rollback();
      throw e;
    }
  }
}
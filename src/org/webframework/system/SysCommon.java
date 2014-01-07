package org.webframework.system;

import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.db.dialect.syntax.SyntaxDialect;
import org.webframework.system.login.User;
import org.webframework.system.manage.cache.OrganCacheService;
import org.webframework.system.manage.entries.SysOrganBean;
import org.webframework.system.manage.entries.SysUserBean;

public class SysCommon
{
  private static OrganCacheService organCacheService = new OrganCacheService();

  public static User getUserById(String czryDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.CZRY_DM,T.CZRY_MC,T.JG_DM,T.SFZHM,T.DHHM,T.SJHM, ");
    sql.append("T.DYDZ,T.DZ,T.MSN,T.QQ,T.XH,T.BZ,T.ZW_DM,T.CJSJ, ");
    sql.append("T1.ZHZT,T1.PASSWORD,T1.ZDHHS,T1.SDSJ, ");
    sql.append("T2.JG_MC,T2.JG_LX_DM,T3.ZW_MC ");
    sql.append("FROM ");
    sql.append("QX_CZRY T LEFT JOIN QX_CZRY_ZHXX T1 ON T.CZRY_DM=T1.CZRY_DM ");
    sql.append("LEFT JOIN QX_ZW T3 ON T.ZW_DM=T3.ZW_DM, ");
    sql.append("QX_JG T2 ");
    sql.append("WHERE ");
    sql.append("T.JG_DM=T2.JG_DM ");
    sql.append("AND T.CZRY_DM=? ");

    Ps ps = new Ps();
    ps.addString(czryDm.toUpperCase());
    return (User)DB.get(sql.toString(), User.class, ps);
  }

  public static WMap getUserMapById(String czryDm)
    throws Exception
  {
    User user = getUserById(czryDm.toUpperCase());
    return (WMap)SysUtils.convertBean2Map(user);
  }

  public static SysUserBean getSysUsersBeanById(String czryDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("CZRY_DM,JG_DM,CZRY_MC,SFZHM,DHHM,SJHM,");
    sql.append("DYDZ,DZ,MSN,QQ,ZW_DM,BZ,XH,CJSJ ");
    sql.append("FROM ");
    sql.append("QX_CZRY ");
    sql.append("WHERE ");
    sql.append("CZRY_DM=? ");
    Ps ps = new Ps();
    ps.addString(czryDm.toUpperCase());
    return (SysUserBean)DB.get(sql.toString(), SysUserBean.class, ps);
  }

  public static SysOrganBean getSysOrganBeanById(String jgDm)
    throws Exception
  {
    return organCacheService.getSysOrganBeanById(jgDm);
  }

  public static SysOrganBean getParentOrganById(String jgDm)
    throws Exception
  {
    return organCacheService.getParentOrganById(jgDm);
  }

  public static String getUserDataAuth(String czryDm, String zd_jgdm)
    throws Exception
  {
    czryDm = czryDm.toUpperCase();
    if (!"?".equals(czryDm)) {
      czryDm = "'" + czryDm + "'";
    }
    StringBuffer sql = new StringBuffer();
    sql.append(" EXISTS(");
    sql.append("   SELECT 1 FROM (");
    sql.append("     SELECT W11_Z1.JG_DM");
    sql.append("     FROM QX_JG_FB W11_Z1");
    sql.append("     WHERE EXISTS (SELECT W11_Z2.JG_LJ");
    sql.append("           FROM QX_JG_FB W11_Z2, QX_CZRY_SJQX W11_Z3");
    sql.append("          WHERE W11_Z2.JG_DM = W11_Z3.JG_DM");
    sql.append("            AND W11_Z3.CZRY_DM = ").append(czryDm);
    sql.append("            AND W11_Z1.JG_LJ LIKE W11_Z2.JG_LJ ").append(DB.getSyntaxDialect().CONCAT()).append(" '%')");
    sql.append("   ) W11_Z4");
    sql.append("   WHERE W11_Z4.JG_DM=").append(zd_jgdm);
    sql.append(" ) ");
    return sql.toString();
  }

  public static String getOrganAuthById(String jgDm, String zd_jgdm)
    throws Exception
  {
    SysOrganBean sysOrganBean = getParentOrganById(jgDm);
    if (sysOrganBean != null) {
      jgDm = sysOrganBean.getJgDm();
    }
    return getLowerAuthById(jgDm, zd_jgdm);
  }

  public static String getLowerAuthById(String jgDm, String zd_jgdm)
    throws Exception
  {
    if (!"?".equals(jgDm)) {
      jgDm = "'" + jgDm + "'";
    }
    StringBuffer sql = new StringBuffer();
    sql.append(" EXISTS(");
    sql.append("       SELECT 1 FROM (");
    sql.append("         SELECT W11_Z1.JG_DM");
    sql.append("         FROM QX_JG_FB W11_Z1");
    sql.append("         WHERE EXISTS (SELECT W11_Z2.JG_LJ");
    sql.append("               FROM QX_JG_FB W11_Z2");
    sql.append("              WHERE W11_Z2.JG_DM = ").append(jgDm);
    sql.append("                AND W11_Z1.JG_LJ LIKE W11_Z2.JG_LJ ").append(DB.getSyntaxDialect().CONCAT()).append(" '%')");
    sql.append("       ) W11_Z3");
    sql.append("       WHERE W11_Z3.JG_DM=").append(zd_jgdm);
    sql.append("     ) ");
    return sql.toString();
  }

  public static String getSameOrganAuthById(String jgDm, String zd_jgdm)
    throws Exception
  {
    SysOrganBean sysOrganBean = getParentOrganById(jgDm);
    String jgdm = null;
    if (sysOrganBean == null)
      jgdm = jgDm;
    else {
      jgdm = sysOrganBean.getJgDm();
    }
    StringBuffer sql = new StringBuffer();
    sql.append(" EXISTS(");
    sql.append("       SELECT 1 FROM (");
    sql.append("         SELECT W11_Z1.JG_DM");
    sql.append("         FROM QX_JG_FB W11_Z1");
    sql.append("         WHERE EXISTS (SELECT W11_Z2.SSJG_LJ");
    sql.append("               FROM QX_JG_FB W11_Z2");
    sql.append("              WHERE W11_Z2.JG_DM = '").append(jgdm).append("'");
    sql.append("                AND W11_Z1.SSJG_LJ=W11_Z2.SSJG_LJ)");
    sql.append("       ) W11_Z3");
    sql.append("       WHERE W11_Z3.JG_DM=").append(zd_jgdm);
    sql.append("     ) ");
    return sql.toString();
  }

  public static void insertSysOnline(String czryDm, String sessionId)
    throws Exception
  {
    StringBuffer sb = new StringBuffer();
    sb.append("INSERT INTO SYS_ONLINE (CZRY_DM, SESSION_ID, TIME) VALUES (?, ?, SYSDATE)");
    Ps ps = new Ps();
    ps.addString(czryDm);
    ps.addString(sessionId);
    DB.update(sb.toString(), ps);
  }

  public static void deleteSysOnline(String sessionId)
    throws Exception
  {
    StringBuffer sb = new StringBuffer();
    sb.append("DELETE FROM SYS_ONLINE WHERE SESSION_ID = ?");
    Ps ps = new Ps();
    ps.addString(sessionId);
    DB.update(sb.toString(), ps);
  }

  public static void deleteSysOnline()
    throws Exception
  {
    StringBuffer sb = new StringBuffer();
    sb.append("DELETE FROM SYS_ONLINE");
    DB.update(sb.toString());
  }

  public static int getCzryOnlineCount(String czrryDm)
    throws Exception
  {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT COUNT(1) COUNT FROM SYS_ONLINE WHERE CZRY_DM = ?");
    Ps ps = new Ps();
    ps.addString(czrryDm);
    return DB.getMap(sb.toString(), ps).getInt("count");
  }
}
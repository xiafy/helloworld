package org.webframework.system.manage.cache;

import java.util.List;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.system.manage.entries.SysMenuItemBean;

public class MenuCacheDao
{
  public List getMenuItemsByCdDm(String cdDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T1.CD_DM,T1.CDX_DM,T1.SJ_CDX_DM,T1.CDX_MC,T1.URI,T1.DKWZ, ");
    sql.append("T1.ZB_X,T1.ZB_Y,T1.CDX_K,T1.CDX_G,T1.XH ");
    sql.append("FROM ");
    sql.append("QX_GNCD T,QX_GNCD_FB T1 ");
    sql.append("WHERE ");
    sql.append("T.CD_DM=T1.CD_DM ");
    sql.append("AND T1.CD_DM=? ");
    sql.append("ORDER BY T1.SJ_CDX_DM,T1.XH ");

    Ps ps = new Ps();
    ps.addString(cdDm);
    return DB.getList(sql.toString(), SysMenuItemBean.class, ps);
  }

  public List getAllMenuItems()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T1.CD_DM,T1.CDX_DM,T1.SJ_CDX_DM,T1.CDX_MC,T1.URI,T1.DKWZ, ");
    sql.append("T1.ZB_X,T1.ZB_Y,T1.CDX_K,T1.CDX_G,T1.XH ");
    sql.append("FROM ");
    sql.append("QX_GNCD T,QX_GNCD_FB T1 ");
    sql.append("WHERE ");
    sql.append("T.CD_DM=T1.CD_DM ");
    sql.append("ORDER BY T1.CD_DM,T1.SJ_CDX_DM,T1.XH ");
    return DB.getList(sql.toString(), SysMenuItemBean.class);
  }

  public List getResByJsDm(String jsDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.URI,T1.JS_DM ");
    sql.append("FROM ");
    sql.append("QX_GNMK_ZY T,QX_JS_ZY T1 ");
    sql.append("WHERE ");
    sql.append("T.ZY_DM=T1.ZY_DM ");
    sql.append("AND T.NMFW_BZ='0' ");
    sql.append("AND T1.JS_DM=? ");
    sql.append("ORDER BY T1.JS_DM ");

    Ps ps = new Ps();
    ps.addString(jsDm);
    return DB.getMapList(sql.toString(), ps);
  }

  public List getAllResUnderAllRole()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.URI,T1.JS_DM ");
    sql.append("FROM ");
    sql.append("QX_GNMK_ZY T,QX_JS_ZY T1 ");
    sql.append("WHERE ");
    sql.append("T.ZY_DM=T1.ZY_DM ");
    sql.append("AND T.NMFW_BZ='0' ");
    sql.append("ORDER BY T1.JS_DM ");
    return DB.getMapList(sql.toString());
  }

  public List getAnonyRes()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("ZY_DM,URI ");
    sql.append("FROM ");
    sql.append("QX_GNMK_ZY ");
    sql.append("WHERE ");
    sql.append("NMFW_BZ='1' ");
    return DB.getMapList(sql.toString());
  }

  public List getRoleByCzryDm(String czryDm)
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("JS_DM ");
    sql.append("FROM ");
    sql.append("QX_CZRY_JS ");
    sql.append("WHERE ");
    sql.append("CZRY_DM=? ");
    Ps ps = new Ps();
    ps.addString(czryDm);
    return DB.getMapList(sql.toString(), ps);
  }

  public String getCurCdDm()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.CD_DM ");
    sql.append("FROM ");
    sql.append("QX_GNCD T ");
    sql.append("WHERE ");
    sql.append("T.QYBZ='1' ");
    List list = DB.getMapList(sql.toString());
    if ((list != null) && (!list.isEmpty())) {
      return ((WMap)list.get(0)).getString("cdDm");
    }
    return null;
  }

  public List getJsDmNoRes()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T1.JS_DM ");
    sql.append("FROM ");
    sql.append("QX_JS T1 ");
    sql.append("WHERE ");
    sql.append("NOT EXISTS(SELECT JS_DM FROM QX_JS_ZY T2 WHERE T1.JS_DM=T2.JS_DM) ");
    return DB.getMapList(sql.toString());
  }

  public List getAllJsDms()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("JS_DM ");
    sql.append("FROM ");
    sql.append("QX_JS ");
    return DB.getMapList(sql.toString());
  }

  public List getAllCdDms()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.CD_DM ");
    sql.append("FROM ");
    sql.append("QX_GNCD T ");
    return DB.getMapList(sql.toString());
  }
}
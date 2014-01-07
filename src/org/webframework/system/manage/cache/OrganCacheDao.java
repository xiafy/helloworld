package org.webframework.system.manage.cache;

import java.util.List;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.system.manage.entries.SysOrganBean;

public class OrganCacheDao
{
  public List getOrgan()
    throws Exception
  {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.JG_DM,T.SJ_JG_DM,T.JG_MC,T.JG_JC,T.JG_LX_DM,T.DZ, ");
    sql.append("T.XZQH_DM,T.DHHM,T.CZBM,T.YZBM,T.DYDZ,T.QYBZ,T.XH,T.BZ, ");
    sql.append("T1.JG_LJ,T1.SSJG_LJ ");
    sql.append("FROM ");
    sql.append("QX_JG T LEFT JOIN QX_JG_FB T1 ON T.JG_DM=T1.JG_DM ");
    sql.append("ORDER BY T.SJ_JG_DM ASC,T.XH ASC ");
    return DB.getList(sql.toString(), SysOrganBean.class);
  }
  public List getUserDataAuth(String czryDm) throws Exception {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.JG_DM ");
    sql.append("FROM ");
    sql.append("QX_CZRY_SJQX T ");
    sql.append("WHERE ");
    sql.append("T.CZRY_DM=? ");
    Ps ps = new Ps();
    int i = 1;
    ps.setString(i++, czryDm);
    return DB.getMapListInOriginal(sql.toString(), ps);
  }

  public boolean isNeedRefreshOrganCache() throws Exception {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("PARAM_VALUE ");
    sql.append("FROM ");
    sql.append("SYS_CS ");
    sql.append("WHERE ");
    sql.append("PARAM_ID='cache_organ' ");
    WMap wmap = DB.getMap(sql.toString());
    if ((wmap != null) && 
      ("1".equals(wmap.getString("paramValue")))) {
      sql.setLength(0);
      sql.append("UPDATE SYS_CS SET ");
      sql.append("PARAM_VALUE='0' ");
      sql.append("WHERE ");
      sql.append("PARAM_ID='cache_organ' ");
      DB.update(sql.toString());
      return true;
    }

    return false;
  }
}
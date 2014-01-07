package org.webframework.system.manage.tree;

import java.util.ArrayList;
import java.util.List;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.system.manage.cache.OrganCacheService;
import org.webframework.system.manage.entries.SysOrganBean;

public class OrganTreeDao
{
  OrganCacheService organCacheService = new OrganCacheService();

  public List getOrganRoot(String jgDm) throws Exception
  {
    List list = null;
    if ((jgDm != null) && (!"".equals(jgDm))) {
      jgDm = this.organCacheService.getParentOrganById(jgDm).getJgDm();
      list = new ArrayList();
      list.add(this.organCacheService.getAclOrganMapById(jgDm));
    } else {
      list = this.organCacheService.getChildOrganById(jgDm);
    }
    return list;
  }

  public List getOrganChildNode(String jgDm) throws Exception
  {
    return this.organCacheService.getChildOrganById(jgDm);
  }

  public List getOrganNode(String jgDm) throws Exception
  {
    List list = new ArrayList();
    list.add(this.organCacheService.getAclOrganMapById(jgDm));
    return list;
  }

  public List getUserByJgDm(String jgDm) throws Exception {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.CZRY_DM,T.CZRY_MC,T.JG_DM,T1.JG_LJ ");
    sql.append("FROM ");
    sql.append("QX_CZRY T LEFT JOIN QX_JG_FB T1 ON T.JG_DM=T1.JG_DM ");
    sql.append("WHERE ");
    sql.append("T.JG_DM=? ");
    sql.append("ORDER BY T.XH ASC ");
    Ps ps = new Ps();
    ps.addString(jgDm);
    return DB.getMapList(sql.toString(), ps);
  }

  public List getTreeRoot(String jgDm) throws Exception {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.JG_DM ID,T.JG_MC NAME,T.SJ_JG_DM P_ID,JG_LX_DM TYPE,QYBZ ");
    sql.append("FROM ");
    sql.append("QX_JG T ");
    sql.append("WHERE ");
    sql.append("T.JG_DM=? ");
    Ps ps = new Ps();
    ps.addString(jgDm);
    return DB.getMapList(sql.toString(), ps);
  }

  public List getTreeNode(String sjJgDm) throws Exception {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("T.JG_DM ID,T.JG_MC NAME,T.SJ_JG_DM P_ID,JG_LX_DM TYPE,QYBZ ");
    sql.append("FROM ");
    sql.append("QX_JG T ");
    sql.append("WHERE ");
    sql.append("T.SJ_JG_DM=? ");
    sql.append("ORDER BY T.XH ASC ");
    Ps ps = new Ps();
    ps.addString(sjJgDm);
    return DB.getMapList(sql.toString(), ps);
  }
}
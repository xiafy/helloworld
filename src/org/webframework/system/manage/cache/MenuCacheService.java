package org.webframework.system.manage.cache;

import java.util.ArrayList;
import java.util.List;
import org.webframework.BeanFactory;
import org.webframework.DB;
import org.webframework.Ps;
import org.webframework.WMap;
import org.webframework.cache.platform.Cache;
import org.webframework.mvc.WebMvcConfig;
import org.webframework.system.SysUtils;
import org.webframework.system.login.User;
import org.webframework.system.login.bean.RequestContext;
import org.webframework.system.manage.entries.SysMenuItemBean;

public class MenuCacheService
{
  MenuCacheDao menuCacheDao = new MenuCacheDao();

  public void cacheMenu()
    throws Exception
  {
    List menus = getAllMenu();

    List roleResList = getAllRes();

    filterMenu(menus, roleResList);

    cacheMenuRoleNoRes(menus);

    cacheAnonyMenuRes(menus);
  }

  private List getAllMenu()
    throws Exception
  {
    List menuItems = this.menuCacheDao.getAllMenuItems();
    List menus = new ArrayList();
    List menu = null;
    String cdDm = null; String curCdDm = "";
    int itemSize = menuItems.size();
    for (int i = 0; i < itemSize; i++) {
      SysMenuItemBean item = (SysMenuItemBean)menuItems.get(i);
      cdDm = item.getCdDm();
      if (!curCdDm.equals(cdDm)) {
        curCdDm = cdDm;
        if (menu != null) {
          menus.add(menu);
        }
        menu = new ArrayList();
      }
      menu.add(item);
    }
    if (menu != null) {
      menus.add(menu);
    }
    return menus;
  }

  private List getAllRes()
    throws Exception
  {
    List res = this.menuCacheDao.getAllResUnderAllRole();
    List roleResList = new ArrayList();
    List resList = null;
    String curJsDm = "";

    int resSize = res.size();
    for (int i = 0; i < resSize; i++) {
      WMap resMap = (WMap)res.get(i);
      String jsDm = resMap.getString("jsDm");
      if (!curJsDm.equals(jsDm)) {
        curJsDm = jsDm;
        if (resList != null) {
          roleResList.add(resList);
        }
        resList = new ArrayList();
      }
      resList.add(resMap);
    }
    if (resList != null) {
      roleResList.add(resList);
    }
    return roleResList;
  }

  private void filterMenu(List menus, List roleResList)
    throws Exception
  {
    int menuSize = menus.size();
    int roleResSize = roleResList.size();

    String cdDm = null;

    for (int i = 0; i < menuSize; i++) {
      List curMenuList = (List)menus.get(i);
      int curMenuSize = curMenuList.size();
      for (int m = 0; m < roleResSize; m++) {
        List finalMenuList = new ArrayList();
        List curRoleResList = (List)roleResList.get(m);
        WMap roleResMap = (WMap)curRoleResList.get(0);
        String jsDm = roleResMap.getString("jsDm");

        for (int j = 0; j < curMenuSize; j++) {
          SysMenuItemBean item = (SysMenuItemBean)curMenuList.get(j);
          cdDm = item.getCdDm();
          String menuUri = item.getUri();
          menuUri = menuUri == null ? "" : menuUri;
          item.setUri(menuUri);
          if ((menuUri.endsWith(".cmd")) || (menuUri.indexOf(".cmd?") != -1)) {
            if (isAccesibleRes(curRoleResList, menuUri, null))
              finalMenuList.add(item);
          }
          else {
            finalMenuList.add(item);
          }
        }
        Cache.put("_Web_key_cache_acl_MenuRole_" + 
          cdDm + "_" + jsDm, finalMenuList);
      }
    }
  }

  private void cacheMenuRoleNoRes(List menus)
    throws Exception
  {
    String cdDm = null;
    int menuSize = menus.size();
    List jsDmNoResList = this.menuCacheDao.getJsDmNoRes();
    int roleNoResSize = jsDmNoResList.size();
    for (int i = 0; i < menuSize; i++) {
      List curMenuList = (List)menus.get(i);
      SysMenuItemBean item = (SysMenuItemBean)curMenuList.get(0);
      cdDm = item.getCdDm();
      for (int j = 0; j < roleNoResSize; j++) {
        String jsDm = ((WMap)jsDmNoResList.get(j)).getString("jsDm");
        Cache.put("_Web_key_cache_acl_MenuRole_" + 
          cdDm + "_" + jsDm, new ArrayList());
      }
    }
  }

  public void cacheAnonyMenuRes(List menus)
    throws Exception
  {
    List resList = this.menuCacheDao.getAnonyRes();

    int menuSize = menus.size();

    String cdDm = null;

    for (int i = 0; i < menuSize; i++) {
      List curMenuList = (List)menus.get(i);
      int curMenuSize = curMenuList.size();
      List finalMenuList = new ArrayList();
      for (int j = 0; j < curMenuSize; j++) {
        SysMenuItemBean item = (SysMenuItemBean)curMenuList.get(j);
        cdDm = item.getCdDm();
        String menuUri = item.getUri();
        menuUri = menuUri == null ? "" : menuUri;
        item.setUri(menuUri);
        if ((menuUri.endsWith(".cmd")) || (menuUri.indexOf(".cmd?") != -1)) {
          if (isAccesibleRes(resList, menuUri, null))
            finalMenuList.add(item);
        }
        else {
          finalMenuList.add(item);
        }
      }
      Cache.put("_Web_key_cache_acl_MenuRole_AnonyMenuRes_" + cdDm, finalMenuList);
    }

    cacheAnonyRes(resList);
  }

  public void cacheAnonyRes(List resList)
    throws Exception
  {
    Cache.put("_Web_key_cache_acl_AnonyRes", resList);
  }

  public void cacheMenuByCdDm(String cdDm)
    throws Exception
  {
    List menu = this.menuCacheDao.getMenuItemsByCdDm(cdDm);
    if ((menu != null) && (!menu.isEmpty())) {
      List menus = new ArrayList();
      menus.add(menu);
      filterMenu(menus, getAllRes());
    }
  }

  public void clearCacheMenuByCdDm(String cdDm)
    throws Exception
  {
    List jsDms = this.menuCacheDao.getAllJsDms();

    int size = jsDms.size();

    for (int i = 0; i < size; i++) {
      WMap map = (WMap)jsDms.get(i);
      String jsDm = map.getString("jsDm");
      Cache.remove("_Web_key_cache_acl_MenuRole_" + 
        cdDm + "_" + jsDm);
    }

    Cache.remove("_Web_key_cache_acl_MenuRole_AnonyMenuRes_" + cdDm);
  }

  public void cacheMenuByJsDm(String jsDm, List menus)
    throws Exception
  {
    List resRoleList = this.menuCacheDao.getResByJsDm(jsDm);
    List list = new ArrayList();
    list.add(resRoleList);
    if (menus == null) {
      menus = getAllMenu();
    }
    if (resRoleList.isEmpty())
    {
      cacheMenuRoleNoRes(menus);
    }
    else
      filterMenu(menus, list);
  }

  public void clearCacheMenuByJsDm(String jsDm)
    throws Exception
  {
    List cdDms = this.menuCacheDao.getAllCdDms();

    int size = cdDms.size();

    for (int i = 0; i < size; i++) {
      WMap map = (WMap)cdDms.get(i);
      String cdDm = map.getString("cdDm");
      Cache.remove("_Web_key_cache_acl_MenuRole_" + 
        cdDm + "_" + jsDm);
    }
  }

  public boolean nmfwBzRes(String cmdPath, RequestContext requestContext)
    throws Exception
  {
    List resList = (List)Cache.get("_Web_key_cache_acl_AnonyRes");
    if (resList == null) {
      resList = this.menuCacheDao.getAnonyRes();
      cacheAnonyRes(resList);
    }
    return isAccesibleRes(resList, cmdPath, requestContext);
  }

  public boolean isAuthAccess(String cmdPath, User user, RequestContext requestContext)
    throws Exception
  {
    List authResList = user.getAuthResList();
    if (authResList == null) {
      StringBuffer sql = new StringBuffer();
      sql.append("SELECT ");
      sql.append("T1.ZY_DM,T1.URI ");
      sql.append("FROM ");
      sql.append("QX_JS_ZY T,QX_GNMK_ZY T1 ");
      sql.append("WHERE ");
      sql.append("EXISTS (SELECT T1.JS_DM FROM QX_CZRY_JS T1 WHERE T.JS_DM=T1.JS_DM AND T1.CZRY_DM=?) ");
      sql.append("AND T.ZY_DM=T1.ZY_DM ");
      Ps ps = new Ps();
      ps.addString(user.getCzryDm());
      authResList = DB.getMapList(sql.toString(), ps);
      user.setAuthResList(authResList);
    }
    return isAccesibleRes(authResList, cmdPath, requestContext);
  }

  private boolean isAccesibleRes(List resList, String uri, RequestContext requestContext)
  {
    if ((resList != null) && (!resList.isEmpty())) {
      int size = resList.size();
      List paths = handleUri(uri);
      int pathsize = paths.size();

      WMap map = null;

      for (int i = 0; i < size; i++) {
        map = (WMap)resList.get(i);
        String resUri = map.getString("uri");
        String[] resUriAry = resUri.split("\\,");
        int len = resUriAry.length;
        for (int j = 0; j < len; j++) {
          resUri = handleResUri(resUriAry[j]);
          for (int m = 0; m < pathsize; m++) {
            if (SysUtils.equals((String)paths.get(m), resUri)) {
              if (requestContext != null) {
                requestContext.setZyDm(map.getString("zyDm"));
              }
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public List handleUri(String uri)
  {
    List urls = new ArrayList();
    String[] ary = uri.split("\\/");
    int len = ary.length;

    for (int i = 0; i < len; i++) {
      String curUri = ary[i];
      if ((curUri.endsWith(".cmd")) || (curUri.indexOf(".cmd?") != -1)) {
        uri = curUri;
        break;
      }

    }

    int index = -1;
    String uriNoMethod;
    String params;
//    String uriNoMethod;
    if ((index = uri.indexOf(".cmd?")) != -1) {
      params = uri.substring(index + 5);
      uriNoMethod = uri.substring(0, index + 4);
    }
    else
    {
//      String uriNoMethod;
      if ((index = uri.indexOf(".cmd")) != -1) {
        params = uri.substring(index + 4);
        uriNoMethod = uri.substring(0, index + 4);
      } else {
        params = "";
        uriNoMethod = uri;
      }
    }
    String[] kvAry = params.split("&");
    String method = "";
    len = kvAry.length;
    for (int i = 0; i < len; i++)
    {
      if (kvAry[i].indexOf("method=") == 0) {
        method = kvAry[i];
      }

    }

    if ("".equals(method)) {
      method = "method=" + ((WebMvcConfig)BeanFactory.getBean("webMvcConfig")).getDefaultCommandMethod();
    }
    String pathWithMethod = uriNoMethod + "?" + method;
    urls.add(pathWithMethod);
    urls.add(uriNoMethod);
    return urls;
  }

  public String handleResUri(String uri)
  {
    if ((!uri.endsWith(".cmd")) && (uri.indexOf(".cmd?") == -1)) {
      uri = uri + ".cmd";
    }

    String[] ary = uri.split("\\/");
    int len = ary.length;

    for (int i = 0; i < len; i++) {
      String curUri = ary[i];
      if ((curUri.endsWith(".cmd")) || (curUri.indexOf(".cmd?") != -1)) {
        uri = curUri;
        break;
      }

    }

    int index = -1;
    String uriNoMethod;
    String params;
//    String uriNoMethod;
    if ((index = uri.indexOf(".cmd?")) != -1) {
      params = uri.substring(index + 5);
      uriNoMethod = uri.substring(0, index + 4);
    }
    else
    {
//      String uriNoMethod;
      if ((index = uri.indexOf(".cmd")) != -1) {
        params = uri.substring(index + 4);
        uriNoMethod = uri.substring(0, index + 4);
      } else {
        params = "";
        uriNoMethod = uri;
      }
    }
    String[] kvAry = params.split("&");
    String method = "";
    len = kvAry.length;
    for (int i = 0; i < len; i++)
    {
      if (kvAry[i].indexOf("method=") == 0) {
        method = kvAry[i];
      }

    }

    if ("".equals(method)) {
      return uriNoMethod;
    }
    return uriNoMethod + "?" + method;
  }
}
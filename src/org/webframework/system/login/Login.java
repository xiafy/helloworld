package org.webframework.system.login;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.webframework.BeanFactory;
import org.webframework.Command;
import org.webframework.WMap;
import org.webframework.cache.platform.Cache;
import org.webframework.exception.WebException;
import org.webframework.mvc.AjaxView;
import org.webframework.mvc.View;
import org.webframework.system.SysConfig;
import org.webframework.system.SysContext;
import org.webframework.system.SysOnlineBindingListener;
import org.webframework.system.log.track.TrackLogin;
import org.webframework.system.login.auth.ILoginAuth;
import org.webframework.system.manage.cache.MenuCacheDao;
import org.webframework.system.manage.cache.MenuCacheService;
import org.webframework.system.manage.entries.SysMenuItemBean;

public class Login extends Command
{
  MenuCacheDao menuCacheDao = new MenuCacheDao();
  MenuCacheService menuCacheService = new MenuCacheService();

  public String index(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    return ((SysConfig)BeanFactory.getBean("sysConfig")).getLoginTheme();
  }

  public String loginByPassword(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    try
    {
      HttpSession session = request.getSession();

      SysContext.reset(session);
      view.setRedirect(true);

      ILoginAuth loginAuth = (ILoginAuth)BeanFactory.getBean("loginAuth");
      if (!loginAuth.validate(request, response, view)) {
        session.setAttribute("_Web_key_login_fail_msg", loginAuth.getErrorMsg());
        TrackLogin.trackFail(request, loginAuth.getCzryDm(), loginAuth.getErrorMsg());
        return ((SysConfig)BeanFactory.getBean("sysConfig")).getLoginPage();
      }

      User user = loginAuth.getUser();

      SysContext.setSysUser(session, user);

      SysContext.setSysUser(user);
      session.setAttribute("SysOnlineBindingListener", new SysOnlineBindingListener(loginAuth.getCzryDm()));

      TrackLogin.track(request, "登录成功", "1");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "org.webframework.system.index.Index.cmd";
  }

  private String getMenuXML(HttpSession session)
    throws Exception
  {
    String cdDm = this.menuCacheDao.getCurCdDm();
    if (cdDm == null) {
      throw new RuntimeException("没有找到当前正在使用的菜单!");
    }

    User user = SysContext.getSysUser(session);
    if (user != null) {
      SysContext.setSysUser(user);
    }
    List roleList = this.menuCacheDao.getRoleByCzryDm(user.getCzryDm());

    List resMenuList = new ArrayList();

    List tlist = null; List menus = null;
    int roleSize = roleList.size();
    for (int i = 0; i < roleSize; i++) {
      String jsDm = ((WMap)roleList.get(i)).getString("jsDm");
      tlist = (List)Cache.get("_Web_key_cache_acl_MenuRole_" + 
        cdDm + "_" + jsDm);
      if (tlist == null) {
        if (menus == null) {
          menus = new ArrayList();
          menus.add(this.menuCacheDao.getMenuItemsByCdDm(cdDm));
        }
        this.menuCacheService.cacheMenuByJsDm(jsDm, menus);
        tlist = (List)Cache.get("_Web_key_cache_acl_MenuRole_" + 
          cdDm + "_" + jsDm);
      }
      if (tlist != null) {
        resMenuList.addAll(tlist);
      }
    }

    List anonyResMenu = (List)Cache.get("_Web_key_cache_acl_MenuRole_AnonyMenuRes_" + cdDm);
    if (anonyResMenu == null) {
      if (menus == null) {
        menus = new ArrayList();
        menus.add(this.menuCacheDao.getMenuItemsByCdDm(cdDm));
      }
      this.menuCacheService.cacheAnonyMenuRes(menus);
      anonyResMenu = (List)Cache.get("_Web_key_cache_acl_MenuRole_AnonyMenuRes_" + cdDm);
    }
    if (anonyResMenu != null) {
      resMenuList.addAll(anonyResMenu);
    }

    int size = resMenuList.size();
    Set set = new HashSet();

    Document tdoc = DocumentHelper.createDocument();
    tdoc.setXMLEncoding("GBK");
    Element troot = tdoc.addElement("root").addAttribute("id", "root");
    for (int i = 0; i < size; i++) {
      SysMenuItemBean item = (SysMenuItemBean)resMenuList.get(i);
      String cdxDm = item.getCdxDm();

      if (!set.add(cdxDm)) continue;
      troot.addElement("menu")
        .addAttribute("id", cdxDm)
        .addAttribute("title", item.getCdxMc())
        .addAttribute("pId", item.getSjCdxDm())
        .addAttribute("seq", String.valueOf(item.getXh()))
        .addAttribute("url", item.getUri())
        .addAttribute("pos", item.getDkwz())
        .addAttribute("zbX", String.valueOf(item.getZbX()))
        .addAttribute("zbY", String.valueOf(item.getZbY()))
        .addAttribute("cdxK", String.valueOf(item.getCdxK()))
        .addAttribute("cdxG", String.valueOf(item.getCdxG()));
    }

    ItemComparator comparator = new ItemComparator();

    Document doc = DocumentHelper.createDocument();
    doc.setXMLEncoding("GBK");
    Element root = doc.addElement("root").addAttribute("id", "root");
    LinkedList eleList = new LinkedList();
    eleList.add(root);

    while ((troot.nodeCount() != 0) && (!eleList.isEmpty())) {
      Element element = (Element)eleList.removeFirst();
      String condition;
      if (element.attributeValue("id").equals("root"))
        condition = "/root/menu[not(@pId)]";
      else {
        condition = "/root/menu[@pId='" + element.attributeValue("id") + "']";
      }
      List list = tdoc.selectNodes(condition);
      size = list.size();
      Collections.sort(list, comparator);
      for (int i = 0; i < size; i++) {
        Element e = (Element)list.get(i);
        eleList.add(e);
        element.add(e.detach());
      }
    }

    List list = doc.selectNodes("//menu[@url='']");
    List t = new ArrayList();
    size = list.size();

    for (int i = 0; i < size; i++) {
      Element e = (Element)list.get(i);
      String path = e.getPath();
      WMap map = new WMap();
      map.put("e", e);
      map.put("count", String.valueOf(path.length() - path.replaceAll("\\/", "").length()));
      t.add(map);
    }
    Collections.sort(t, new PathComparator());
    size = t.size();
    for (int i = 0; i < size; i++) {
      Element e = (Element)((WMap)t.get(i)).get("e");
      if (e.nodeCount() == 0) {
        e.detach();
        doc.remove(e);
      }
    }
    String menuxml = doc.asXML();
    menuxml = menuxml.replaceAll("&", "&amp;");
    return menuxml;
  }

  public String getMenuCachedXML(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    HttpSession session = request.getSession();
    String menuXml = SysContext.getSysUser(session).getMenuXml();
    if (menuXml == null) {
      menuXml = getMenuXML(session);
      SysContext.getSysUser(session).setMenuXml(menuXml);
    }
    response.setContentType("text/xml; charset=gbk");
    PrintWriter writer = response.getWriter();
    writer.write(menuXml);
    writer.flush();
    writer.close();
    return null;
  }

  public String logout(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    TrackLogin.track(request, "注销", "2");
    SysContext.reset(request.getSession());
    request.getSession().invalidate();
    view.setRedirect(true);
    return ((SysConfig)BeanFactory.getBean("sysConfig")).getLoginPage();
  }

  public void quit(HttpServletRequest request, HttpServletResponse response, AjaxView view)
    throws Exception
  {
    TrackLogin.track(request, "退出", "2");
    request.getSession().invalidate();
  }

  class ItemComparator
    implements Comparator
  {
    ItemComparator()
    {
    }

    public int compare(Object e1, Object e2)
    {
      return Integer.parseInt(((Element)e1).attributeValue("seq")) - 
        Integer.parseInt(((Element)e2).attributeValue("seq"));
    }
  }
  class PathComparator implements Comparator {
    PathComparator() {
    }
    public int compare(Object e1, Object e2) {
      try { return ((WMap)e2).getInt("count") - ((WMap)e1).getInt("count");
      } catch (WebException e) {
        e.printStackTrace();
      }
      return 0;
    }
  }
}
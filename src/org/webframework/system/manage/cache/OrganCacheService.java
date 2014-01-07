package org.webframework.system.manage.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.webframework.WMap;
import org.webframework.cache.platform.Cache;
import org.webframework.system.SysUtils;
import org.webframework.system.manage.entries.SysOrganBean;

public class OrganCacheService
{
  OrganCacheDao organCacheDao = new OrganCacheDao();

  public void cacheOrgan() throws Exception
  {
    List organList = this.organCacheDao.getOrgan();

    int size = organList.size();

    Document tdoc = DocumentHelper.createDocument();
    tdoc.setXMLEncoding("GBK");
    Element troot = tdoc.addElement("root");
    for (int i = 0; i < size; i++) {
      SysOrganBean organ = (SysOrganBean)organList.get(i);
      troot.addElement("organ")
        .addAttribute("jgDm", organ.getJgDm())
        .addAttribute("sjJgDm", organ.getSjJgDm())
        .addAttribute("jgMc", organ.getJgMc())
        .addAttribute("jgJc", organ.getJgJc())
        .addAttribute("jgLxDm", organ.getJgLxDm())
        .addAttribute("dz", organ.getDz())
        .addAttribute("xzqhDm", organ.getXzqhDm())
        .addAttribute("dhhm", organ.getDhhm())
        .addAttribute("czbm", organ.getCzbm())
        .addAttribute("yzbm", organ.getYzbm())
        .addAttribute("dydz", organ.getDydz())
        .addAttribute("qybz", organ.getQybz())
        .addAttribute("xh", String.valueOf(organ.getXh()))
        .addAttribute("bz", organ.getBz())
        .addAttribute("jgLj", organ.getJgLj())
        .addAttribute("ssjgLj", organ.getSsjgLj())
        .addAttribute("isParent", "false");
    }

    XhComparator comparator = new XhComparator();

    Document doc = DocumentHelper.createDocument();
    doc.setXMLEncoding("GBK");
    Element root = doc.addElement("root");
    LinkedList eleList = new LinkedList();
    eleList.add(root);

    while ((troot.nodeCount() != 0) && (!eleList.isEmpty())) {
      Element element = (Element)eleList.removeFirst();
      String condition;
//      String condition;
      if ("root".equals(element.getName()))
        condition = "/root/organ[not(@sjJgDm)]";
      else {
        condition = "/root/organ[@sjJgDm='" + element.attributeValue("jgDm") + "']";
      }
      List list = tdoc.selectNodes(condition);
      size = list.size();
      if (size > 0) {
        element.addAttribute("isParent", "true");
        Collections.sort(list, comparator);
        for (int i = 0; i < size; i++) {
          Element e = (Element)list.get(i);
          eleList.add(e);
          element.add(e.detach());
        }
      }
    }
    Cache.put("_Web_key_cache_acl_Organ", doc);
  }

  public SysOrganBean getSysOrganBeanById(String jgDm) throws Exception {
    if (this.organCacheDao.isNeedRefreshOrganCache()) {
      cacheOrgan();
    }
    Document doc = (Document)Cache.get("_Web_key_cache_acl_Organ");
    if (doc == null) {
      cacheOrgan();
      doc = (Document)Cache.get("_Web_key_cache_acl_Organ");
    }
    List list = doc.selectNodes("//organ[@jgDm='" + jgDm + "']");
    if ((list != null) && (!list.isEmpty())) {
      return convertEle2Bean((Element)list.get(0));
    }
    return null;
  }
  public Map getAclOrganMapById(String jgDm) throws Exception {
    return SysUtils.convertBean2Map(getSysOrganBeanById(jgDm));
  }

  public SysOrganBean getParentOrganById(String jgDm) throws Exception {
    if (this.organCacheDao.isNeedRefreshOrganCache()) {
      cacheOrgan();
    }
    Document doc = (Document)Cache.get("_Web_key_cache_acl_Organ");
    if (doc == null) {
      cacheOrgan();
      doc = (Document)Cache.get("_Web_key_cache_acl_Organ");
    }
    List list = doc.selectNodes("//organ[@jgDm='" + jgDm + "']");
    if ((list != null) && (!list.isEmpty())) {
      Element element = (Element)list.get(0);
      if (!"01".equals(element.attributeValue("jgLxDm"))) {
        while (element != null) {
          element = element.getParent();
          if ("01".equals(element.attributeValue("jgLxDm"))) {
            break;
          }
        }
      }
      return convertEle2Bean(element);
    }
    return null;
  }
  public Map getParentOrganMapById(String jgDm) throws Exception {
    return SysUtils.convertBean2Map(getParentOrganById(jgDm));
  }

  public List getChildOrganById(String jgDm) throws Exception {
    if (this.organCacheDao.isNeedRefreshOrganCache()) {
      cacheOrgan();
    }
    Document doc = (Document)Cache.get("_Web_key_cache_acl_Organ");
    if (doc == null) {
      cacheOrgan();
      doc = (Document)Cache.get("_Web_key_cache_acl_Organ");
    }
    List result = new ArrayList();
    List list = null;
    if ((jgDm == null) || ("".equals(jgDm)))
      list = doc.selectNodes("/root/organ[not(@sjJgDm)]");
    else {
      list = doc.selectNodes("//organ[@sjJgDm='" + jgDm + "']");
    }
    if ((list != null) && (!list.isEmpty())) {
      int size = list.size();
      for (int i = 0; i < size; i++) {
        result.add(convertEle2WMap((Element)list.get(i)));
      }
    }
    return result;
  }
  private SysOrganBean convertEle2Bean(Element element) {
    SysOrganBean aclOrganBean = null;
    if (element != null) {
      aclOrganBean = new SysOrganBean();
      aclOrganBean.setJgDm(element.attributeValue("jgDm"));
      aclOrganBean.setJgJc(element.attributeValue("jgJc"));
      aclOrganBean.setSjJgDm(element.attributeValue("sjJgDm"));
      aclOrganBean.setJgMc(element.attributeValue("jgMc"));
      aclOrganBean.setJgLxDm(element.attributeValue("jgLxDm"));
      aclOrganBean.setDz(element.attributeValue("dz"));
      aclOrganBean.setXzqhDm(element.attributeValue("xzqhDm"));
      aclOrganBean.setDhhm(element.attributeValue("tel"));
      aclOrganBean.setCzbm(element.attributeValue("czbm"));
      aclOrganBean.setYzbm(element.attributeValue("yzbm"));
      aclOrganBean.setDydz(element.attributeValue("dydz"));
      aclOrganBean.setQybz(element.attributeValue("qybz"));
      aclOrganBean.setXh(Integer.parseInt(element.attributeValue("xh")));
      aclOrganBean.setBz(element.attributeValue("bz"));
      aclOrganBean.setJgLj(element.attributeValue("jgLj"));
      aclOrganBean.setSsjgLj(element.attributeValue("ssjgLj"));
      aclOrganBean.setIsParent(element.attributeValue("isParent"));
    }
    return aclOrganBean;
  }
  private WMap convertEle2WMap(Element element) {
    WMap wmap = null;
    if (element != null) {
      wmap = new WMap();
      wmap.put("jgDm", element.attributeValue("jgDm"));
      wmap.put("jgJc", element.attributeValue("jgJc"));
      wmap.put("sjJgDm", element.attributeValue("sjJgDm"));
      wmap.put("jgMc", element.attributeValue("jgMc"));
      wmap.put("jgLxDm", element.attributeValue("jgLxDm"));
      wmap.put("address", element.attributeValue("address"));
      wmap.put("xzqhDm", element.attributeValue("xzqhDm"));
      wmap.put("tel", element.attributeValue("tel"));
      wmap.put("czbm", element.attributeValue("czbm"));
      wmap.put("yzbm", element.attributeValue("yzbm"));
      wmap.put("dydz", element.attributeValue("dydz"));
      wmap.put("qybz", element.attributeValue("qybz"));
      wmap.put("xh", element.attributeValue("xh"));
      wmap.put("bz", element.attributeValue("bz"));
      wmap.put("jgLj", element.attributeValue("jgLj"));
      wmap.put("ssjgLj", element.attributeValue("ssjgLj"));
      wmap.put("isParent", element.attributeValue("isParent"));
    }
    return wmap;
  }
  class XhComparator implements Comparator {
    XhComparator() {
    }
    public int compare(Object e1, Object e2) { return Integer.parseInt(((Element)e1).attributeValue("xh")) - 
        Integer.parseInt(((Element)e2).attributeValue("xh"));
    }
  }
}
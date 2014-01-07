package org.webframework.cache.memcached;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MemcacheStatsSlab
{
  private String serverHost;
  private Map slabs;

  public MemcacheStatsSlab()
  {
    this.slabs = new TreeMap(new SlabKeyComparator());

    Slab node = new Slab();
    node.setSlabNum("0");
    this.slabs.put("0", node);
  }

  public void addSlab(String slabNum, String slabInfo)
  {
    if (slabNum.indexOf(":") < 0) {
      ((Slab)this.slabs.get("0")).getSlabInfo().put(slabNum, slabInfo);
      return;
    }

    String num = slabNum.substring(0, slabNum.indexOf(":"));

    Slab node = (Slab)this.slabs.get(num);

    if (node == null) {
      node = new Slab();
      node.setSlabNum(num);
    }

    node.getSlabInfo().put(slabNum.substring(slabNum.indexOf(":") + 1), 
      slabInfo);

    this.slabs.put(num, node);
  }

  public String getServerHost() {
    return this.serverHost;
  }

  public void setServerHost(String serverHost) {
    this.serverHost = serverHost;
  }

  public Map getSlabs() {
    return this.slabs;
  }

  public void setSlabs(Map slabs) {
    this.slabs = slabs;
  }

  public class Slab
  {
    private String slabNum;
    private Map slabInfo = new TreeMap();

    public Slab() {
    }
    public String getSlabNum() { return this.slabNum; }

    public void setSlabNum(String slabNum)
    {
      this.slabNum = slabNum;
    }

    public Map getSlabInfo() {
      return this.slabInfo;
    }

    public void setSlabInfo(Map slabInfo) {
      this.slabInfo = slabInfo;
    }

    public String toString() {
      if (this.slabNum.equals("0")) {
        return "Total Slab Info : " + 
          this.slabInfo.toString();
      }
      return "slabNum:  " + this.slabNum + 
        ",slabInfo:  " + this.slabInfo.toString();
    }
  }
  class SlabKeyComparator implements Comparator {
    SlabKeyComparator() {
    }

    public int compare(Object o1, Object o2) {
      int result = 0;
      try
      {
        int i1 = Integer.parseInt((String)o1);

        int i2 = Integer.parseInt((String)o2);

        result = i1 - i2;
      } catch (Exception ex) {
        System.out.println(ex);
      }

      return result;
    }
  }
}
package org.webframework.cache.memcached;

import java.util.List;

public class MemcachedClientCluster
{
  private String name;
  private List caches;
  private String mode = "active";

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List getCaches() {
    return this.caches;
  }

  public void setCaches(List caches) {
    this.caches = caches;
  }

  public String getMode() {
    return this.mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }
}
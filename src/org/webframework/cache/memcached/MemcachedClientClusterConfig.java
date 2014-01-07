package org.webframework.cache.memcached;

public class MemcachedClientClusterConfig
{
  private String name;
  private String[] memCachedClients;
  private String mode = "active";
  public static final String CLUSTER_MODE_ACTIVE = "active";
  public static final String CLUSTER_MODE_STANDBY = "standby";
  public static final String CLUSTER_MODE_NONE = "none";

  public String getName()
  {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getMemCachedClients() {
    return this.memCachedClients;
  }

  public void setMemCachedClients(String[] memCachedClients) {
    this.memCachedClients = memCachedClients;
  }

  public String getMode() {
    return this.mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }
}
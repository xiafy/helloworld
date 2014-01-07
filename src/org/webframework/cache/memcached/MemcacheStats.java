package org.webframework.cache.memcached;

import java.io.Serializable;

public class MemcacheStats
  implements Serializable
{
  private String serverHost;
  private String statInfo;

  public String getServerHost()
  {
    return this.serverHost;
  }

  public void setServerHost(String serverHost) {
    this.serverHost = serverHost;
  }

  public String getStatInfo() {
    return this.statInfo;
  }

  public void setStatInfo(String statInfo) {
    this.statInfo = statInfo;
  }
}
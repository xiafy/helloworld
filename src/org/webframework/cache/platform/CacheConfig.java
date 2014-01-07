package org.webframework.cache.platform;

public class CacheConfig
{
  private boolean on;
  private String configPath;

  public boolean isOn()
  {
    return this.on;
  }
  public void setOn(boolean on) {
    this.on = on;
  }
  public String getConfigPath() {
    return this.configPath;
  }
  public void setConfigPath(String configPath) {
    this.configPath = configPath;
  }
}
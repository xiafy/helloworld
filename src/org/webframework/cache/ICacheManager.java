package org.webframework.cache;

import java.net.URL;

public abstract interface ICacheManager
{
  public abstract ICache getCache(String paramString);

  public abstract void setConfigFile(String paramString);

  public abstract void setConfigFileUrl(URL paramURL);

  public abstract void start();

  public abstract void stop();

  public abstract void reload(String paramString);

  public abstract void clusterCopy(String paramString1, String paramString2);

  public abstract void setResponseStatInterval(int paramInt);
}
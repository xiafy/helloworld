package org.webframework.cache.memcached;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.cache.IMemcachedCache;
import org.webframework.cache.memcached.client.MemCachedClient;

public class MemCachedClientHelper
{
  private static final Log Logger = LogFactory.getLog(MemCachedClientHelper.class);
  private MemCachedClient cacheClient;
  private MemcachedCacheManager cacheManager;
  private IMemcachedCache memcachedCache;
  private String cacheName;

  public List getClusterCache()
  {
    List result = new ArrayList();

    if (hasCluster()) {
      MemcachedClientCluster cluster = (MemcachedClientCluster)this.cacheManager
        .getCache2cluster().get(this.memcachedCache);
      List nodelist = cluster.getCaches();
      int size = nodelist.size();

      for (int i = 0; i < size; i++) {
        Object node = nodelist.get(i);
        if ((node instanceof MemcachedCache)) {
          result.add(((MemcachedCache)node).getHelper().getInnerCacheClient());
        }
      }
    }

    return result;
  }

  protected boolean hasCluster() {
    boolean result = false;

    if ((this.memcachedCache != null) && (this.cacheManager != null)) {
      MemcachedClientCluster cluster = (MemcachedClientCluster)this.cacheManager
        .getCache2cluster().get(this.memcachedCache);

      if ((cluster != null) && (cluster.getCaches() != null) && 
        (cluster.getCaches().size() > 0)) {
        result = true;
      }
    }
    return result;
  }

  protected String getClusterMode() {
    String mode = "none";

    if ((this.memcachedCache != null) && (this.cacheManager != null)) {
      MemcachedClientCluster cluster = (MemcachedClientCluster)this.cacheManager
        .getCache2cluster().get(this.memcachedCache);

      if ((cluster != null) && (cluster.getCaches() != null) && 
        (cluster.getCaches().size() > 0) && (
        (cluster.getMode().equals("active")) || 
        (cluster.getMode().equals("standby")))) {
        mode = cluster.getMode();
      }

    }

    return mode;
  }

  public MemCachedClient getInnerCacheClient() {
    if (this.cacheClient == null) {
      Logger.error("cacheClient can't be injected into MemcachedCacheHelper");
      throw new RuntimeException(
        "cacheClient can't be injected into MemcachedCacheHelper");
    }
    return this.cacheClient;
  }

  public MemCachedClient getCacheClient(Object key) {
    if (this.cacheClient == null) {
      Logger.error("cacheClient can't be injected into MemcachedCacheHelper");
      throw new RuntimeException(
        "cacheClient can't be injected into MemcachedCacheHelper");
    }

    if (hasCluster()) {
      List clusters = getClusterCache();
      long keyhash = key.hashCode();
      int index = (int)keyhash % clusters.size();
      if (index < 0) {
        index *= -1;
      }
      return (MemCachedClient)clusters.get(index);
    }
    return this.cacheClient;
  }

  public void setCacheClient(MemCachedClient cacheClient) {
    this.cacheClient = cacheClient;
  }

  public MemcachedCacheManager getCacheManager() {
    return this.cacheManager;
  }

  public void setCacheManager(MemcachedCacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public IMemcachedCache getMemcachedCache() {
    return this.memcachedCache;
  }

  public void setMemcachedCache(IMemcachedCache memcachedCache) {
    this.memcachedCache = memcachedCache;
  }

  public String getCacheName() {
    return this.cacheName;
  }

  public void setCacheName(String cacheName) {
    this.cacheName = cacheName;
  }
}
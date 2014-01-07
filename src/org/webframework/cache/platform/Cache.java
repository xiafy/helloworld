package org.webframework.cache.platform;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.webframework.cache.ICacheManager;
import org.webframework.cache.IMemcachedCache;
import org.webframework.cache.impl.DefaultCacheImpl;
import org.webframework.cache.memcached.MemcacheStats;
import org.webframework.cache.memcached.MemcacheStatsSlab;
import org.webframework.cache.memcached.MemcachedCacheManager;
import org.webframework.cache.memcached.MemcachedResponse;

public class Cache
{
  private static Cache cache = new Cache();
  private static boolean on;
  private static ICacheManager cacheManager;
  private static DefaultCacheImpl defaultCache = new DefaultCacheImpl();

  public static Cache getInstance()
  {
    return cache;
  }

  public static Object put(String key, Object value)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().put(key, value);
    }
    if (!on) {
      return defaultCache.put(key, value);
    }
    return value;
  }

  public static Object put(String key, Object value, Date expiry)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().put(key, value, expiry);
    }
    if (!on) {
      return defaultCache.put(key, value, expiry);
    }
    return value;
  }

  public static Object put(String key, Object value, int TTL)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().put(key, value, TTL);
    }
    if (!on) {
      return defaultCache.put(key, value, TTL);
    }
    return value;
  }

  public static Object get(String key)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().get(key);
    }
    if (!on) {
      return defaultCache.get(key);
    }
    return null;
  }

  public static Object remove(String key)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().remove(key);
    }
    if (!on) {
      return defaultCache.remove(key);
    }
    return Boolean.FALSE;
  }

  public static boolean clear()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().clear();
    }
    if (!on) {
      return defaultCache.clear();
    }
    return false;
  }

  public static int size()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().size();
    }
    if (!on) {
      return defaultCache.size();
    }
    return -1;
  }

  public static Set keySet()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().keySet();
    }
    if (!on) {
      return defaultCache.keySet();
    }
    return new HashSet();
  }

  public static Collection values()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().values();
    }
    if (!on) {
      return defaultCache.values();
    }
    return new HashSet();
  }

  public static boolean containsKey(String key)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().containsKey(key);
    }
    if (!on) {
      return defaultCache.containsKey(key);
    }
    return false;
  }

  public void destroy()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      cache.getMemcache().destroy();
    }
    if (!on)
      defaultCache.destroy();
  }

  public static Object get(String key, int localTTL)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().get(key, localTTL);
    }
    if (!on) {
      return defaultCache.get(key);
    }
    return null;
  }

  public static Object[] getMultiArray(String[] keys)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().getMultiArray(keys);
    }
    if (!on) {
      return defaultCache.getMultiArray(keys);
    }
    return null;
  }

  public static Map getMulti(String[] keys)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().getMulti(keys);
    }
    if (!on) {
      return defaultCache.getMulti(keys);
    }
    return null;
  }

  public static long incr(String key, long inc)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().incr(key, inc);
    }
    if (!on) {
      return -1L;
    }
    return -1L;
  }

  public static long decr(String key, long decr)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().decr(key, decr);
    }
    if (!on) {
      return -1L;
    }
    return -1L;
  }

  public static long addOrIncr(String key, long inc)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().addOrIncr(key, inc);
    }
    if (!on) {
      return -1L;
    }
    return -1L;
  }

  public static long addOrDecr(String key, long decr)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().addOrDecr(key, decr);
    }
    if (!on) {
      return -1L;
    }
    return -1L;
  }

  public static void storeCounter(String key, long count)
    throws Exception
  {
    if ((on) && (cacheManager != null))
      cache.getMemcache().storeCounter(key, count);
  }

  public static long getCounter(String key)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().getCounter(key);
    }
    if (!on) {
      return -1L;
    }
    return -1L;
  }

  public static Set keySet(boolean fast)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().keySet(fast);
    }
    if (!on) {
      return defaultCache.keySet();
    }
    return null;
  }

  public static MemcacheStatsSlab[] statsSlabs()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().statsSlabs();
    }
    if (!on) {
      return null;
    }
    return null;
  }

  public static MemcacheStats[] stats()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().stats();
    }
    if (!on) {
      return null;
    }
    return null;
  }

  public static Map statsItems()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().statsItems();
    }
    if (!on) {
      return null;
    }
    return null;
  }

  public static MemcachedResponse statCacheResponse()
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().statCacheResponse();
    }
    if (!on) {
      return null;
    }
    return null;
  }

  public static void setStatisticsInterval(long checkInterval)
    throws Exception
  {
    if ((on) && (cacheManager != null))
      cache.getMemcache().setStatisticsInterval(checkInterval);
  }

  public static boolean add(String key, Object value)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().add(key, value);
    }
    if (!on) {
      return defaultCache.add(key, value);
    }
    return false;
  }

  public static boolean add(String key, Object value, Date expiry)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().add(key, value, expiry);
    }
    if (!on) {
      defaultCache.put(key, value, expiry);
      return true;
    }
    return false;
  }

  public static boolean replace(String key, Object value)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().replace(key, value);
    }
    if (!on) {
      return defaultCache.replace(key, value);
    }
    return false;
  }

  public static boolean replace(String key, Object value, Date expiry)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      return cache.getMemcache().replace(key, value, expiry);
    }
    if (!on) {
      return defaultCache.replace(key, value, expiry);
    }
    return false;
  }

  public static void asynPut(String key, Object value)
    throws Exception
  {
    if ((on) && (cacheManager != null)) {
      cache.getMemcache().asynPut(key, value);
    }
    if (!on)
      defaultCache.put(key, value);
  }

  public static void asynAddOrDecr(String key, long decr)
    throws Exception
  {
    if ((on) && (cacheManager != null))
      cache.getMemcache().asynAddOrDecr(key, decr);
  }

  public static void asynAddOrIncr(String key, long incr)
    throws Exception
  {
    if ((on) && (cacheManager != null))
      cache.getMemcache().asynAddOrIncr(key, incr);
  }

  public static void asynDecr(String key, long decr)
    throws Exception
  {
    if ((on) && (cacheManager != null))
      cache.getMemcache().asynDecr(key, decr);
  }

  public static void asynIncr(String key, long incr)
    throws Exception
  {
    if ((on) && (cacheManager != null))
      cache.getMemcache().asynIncr(key, incr);
  }

  public static void asynStoreCounter(String key, long count)
    throws Exception
  {
    if ((on) && (cacheManager != null))
      cache.getMemcache().asynStoreCounter(key, count);
  }

  public MemcachedCacheManager getMemcachedCacheManager()
  {
    if ((cacheManager instanceof MemcachedCacheManager)) {
      return (MemcachedCacheManager)cacheManager;
    }
    return null;
  }

  public IMemcachedCache getMemcache()
    throws Exception
  {
    MemcachedCacheManager manager = getMemcachedCacheManager();
    if (manager != null) {
      Set keyset = manager.getCachepool().keySet();
      if (keyset.size() > 0) {
        String cachename = (String)keyset.iterator().next();
        return (IMemcachedCache)manager.getCache(cachename);
      }
      throw new Exception("未找到任何cache,请检查cache配置文件!");
    }

    throw new Exception("当前cache系统不支持此cache管理器!");
  }

  public IMemcachedCache getCache(String name)
    throws Exception
  {
    return (IMemcachedCache)cacheManager.getCache(name);
  }

  public boolean isOn() {
    return on;
  }
  public void setOn(boolean on) {
    on = on;
  }
  public ICacheManager getCacheManager() {
    return cacheManager;
  }
  public void setCacheManager(ICacheManager cacheManager) {
    cacheManager = cacheManager;
  }
}
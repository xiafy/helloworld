package org.webframework.cache.impl;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.cache.ICache;

public class DefaultCacheImpl
  implements ICache
{
  private static final Log Logger = LogFactory.getLog(DefaultCacheImpl.class);
  ConcurrentHashMap[] caches;
  ConcurrentHashMap expiryCache;
  private ScheduledExecutorService scheduleService;
  private int expiryInterval = 10;

  private int moduleSize = 10;

  public DefaultCacheImpl() {
    init();
  }

  public DefaultCacheImpl(int expiryInterval, int moduleSize) {
    this.expiryInterval = expiryInterval;
    this.moduleSize = moduleSize;
    init();
  }

  private void init() {
    this.caches = new ConcurrentHashMap[this.moduleSize];

    for (int i = 0; i < this.moduleSize; i++) {
      this.caches[i] = new ConcurrentHashMap();
    }
    this.expiryCache = new ConcurrentHashMap();

    this.scheduleService = Executors.newScheduledThreadPool(1);

    this.scheduleService.scheduleAtFixedRate(
      new CheckOutOfDateSchedule(this.caches, 
      this.expiryCache), 0L, this.expiryInterval * 60, TimeUnit.SECONDS);

    if (Logger.isInfoEnabled())
      Logger.info("DefaultCache CheckService is start!");
  }

  public boolean clear() {
    if (this.caches != null) {
      int len = this.caches.length;
      for (int i = 0; i < len; i++) {
        this.caches[i].clear();
      }
    }

    if (this.expiryCache != null) {
      this.expiryCache.clear();
    }
    return true;
  }

  public boolean containsKey(String key) {
    checkValidate(key);
    return getCache(key).containsKey(key);
  }

  public Object get(String key) {
    checkValidate(key);
    return getCache(key).get(key);
  }

  public Set keySet() {
    checkAll();
    return this.expiryCache.keySet();
  }

  public Object put(String key, Object value) {
    Object result = getCache(key).put(key, value);
    this.expiryCache.put(key, new Long(-1L));

    return result;
  }

  public Object put(String key, Object value, Date expiry) {
    Object result = getCache(key).put(key, value);
    this.expiryCache.put(key, new Long(expiry.getTime()));

    return result;
  }

  public Object remove(String key) {
    Object result = getCache(key).remove(key);
    this.expiryCache.remove(key);
    return result;
  }

  public int size() {
    checkAll();

    return this.expiryCache.size();
  }

  public Collection values() {
    checkAll();

    Collection values = new ArrayList();

    int len = this.caches.length;
    for (int i = 0; i < len; i++) {
      values.addAll(this.caches[i].values());
    }
    return values;
  }

  public Object[] getMultiArray(String[] keys) {
    int len = keys.length;
    Object[] objs = new Object[len];

    for (int i = 0; i < len; i++) {
      String key = keys[i];
      checkValidate(key);
      objs[i] = getCache(key).get(key);
    }
    return objs;
  }

  public Map getMulti(String[] keys) {
    int len = keys.length;
    Map map = new HashMap();

    for (int i = 0; i < len; i++) {
      String key = keys[i];
      checkValidate(key);
      map.put(key, getCache(key).get(key));
    }
    return map;
  }

  public boolean add(String key, Object value) {
    Object result = get(key);
    if (result == null) {
      put(key, value);
      return true;
    }
    return false;
  }

  public boolean replace(String key, Object value) {
    Object result = get(key);
    if (result != null) {
      put(key, value);
      return true;
    }
    return false;
  }

  public boolean replace(String key, Object value, Date expiry) {
    Object result = get(key);
    if (result != null) {
      put(key, value, expiry);
      return true;
    }
    return false;
  }

  private ConcurrentHashMap getCache(Object key) {
    long hashCode = key.hashCode();

    if (hashCode < 0L) {
      hashCode = -hashCode;
    }
    int moudleNum = (int)hashCode % this.moduleSize;

    return this.caches[moudleNum];
  }

  private void checkValidate(Object key) {
    Object value = this.expiryCache.get(key);
    if ((key != null) && (value != null) && (((Long)value).longValue() != -1L) && 
      (new Date(((Long)value).longValue()).before(new Date()))) {
      getCache(key).remove(key);
      this.expiryCache.remove(key);
    }
  }

  private void checkAll() {
    Iterator iter = this.expiryCache.keySet().iterator();

    while (iter.hasNext()) {
      String key = (String)iter.next();
      checkValidate(key);
    }
  }

  public Object put(String key, Object value, int TTL)
  {
    Object result = getCache(key).put(key, value);

    Calendar calendar = Calendar.getInstance();
    calendar.add(13, TTL);
    this.expiryCache.put(key, new Long(calendar.getTime().getTime()));

    return result;
  }

  public void destroy() {
    try {
      clear();

      if (this.scheduleService != null) {
        this.scheduleService.shutdown();
      }
      this.scheduleService = null;
    } catch (Exception ex) {
      Logger.error(ex);
    }
  }

  class CheckOutOfDateSchedule
    implements Runnable
  {
    ConcurrentHashMap[] caches;
    ConcurrentHashMap expiryCache;

    public CheckOutOfDateSchedule(ConcurrentHashMap[] caches, ConcurrentHashMap expiryCache)
    {
      this.caches = caches;
      this.expiryCache = expiryCache;
    }

    public void run() {
      check();
    }

    public void check() {
      try {
        int len = this.caches.length;
        for (int i = 0; i < len; i++) {
          ConcurrentHashMap cache = this.caches[i];
          Iterator keys = cache.keySet().iterator();
          while (keys.hasNext()) {
            String key = (String)keys.next();
            if (this.expiryCache.get(key) == null) {
              continue;
            }
            long date = ((Long)this.expiryCache.get(key)).longValue();

            if ((date > 0L) && (new Date(date).before(new Date()))) {
              this.expiryCache.remove(key);
              cache.remove(key);
            }
          }
        }
      }
      catch (Exception ex) {
        DefaultCacheImpl.Logger.info("DefaultCache CheckService is start!");
      }
    }
  }
}
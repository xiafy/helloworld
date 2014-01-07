package org.webframework.cache.memcached;

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.cache.ICache;
import org.webframework.cache.IMemcachedCache;
import org.webframework.cache.impl.DefaultCacheImpl;
import org.webframework.cache.memcached.client.MemCachedClient;

public class MemcachedCache
  implements IMemcachedCache
{
  private static final Log Logger = LogFactory.getLog(MemcachedCache.class);
  private MemCachedClientHelper helper;
  private ICache localCache;
  private ClusterProcessor processor;
  private StatisticsTask task;
  private long statisticsInterval = 300L;
  static final String CACHE_STATUS_RESPONSE = "cacheStatusResponse";
  private LinkedBlockingQueue dataQueue;

  public MemcachedCache(MemCachedClientHelper helper, int statisticsInterval)
  {
    this.helper = helper;

    this.dataQueue = new LinkedBlockingQueue();
    this.localCache = new DefaultCacheImpl();

    if (statisticsInterval > 0) {
      this.statisticsInterval = statisticsInterval;
      this.task = new StatisticsTask();
      this.task.setDaemon(true);
      this.task.start();
    }

    this.processor = new ClusterProcessor(this.dataQueue, helper);
    this.processor.setDaemon(true);
    this.processor.start();
  }

  public boolean clear() {
    boolean result = false;

    if (this.helper.hasCluster()) {
      List caches = this.helper.getClusterCache();
      int size = caches.size();

      for (int i = 0; i < size; i++) {
        MemCachedClient cache = (MemCachedClient)caches.get(i);
        try {
          result = cache.flushAll(null);
        } catch (Exception ex) {
          Logger.error(
            new StringBuffer(this.helper.getCacheName()).append(" cluster clear error"), ex);
          result = false;
        }
      }
      return result;
    }

    return this.helper.getInnerCacheClient().flushAll(null);
  }

  public Map getMulti(String[] keys)
  {
    if ((keys == null) || (keys.length <= 0)) {
      return null;
    }
    Map result = getCacheClient(keys[0]).getMulti(keys);

    if (result != null) {
      return result;
    }
    if (this.helper.hasCluster()) {
      List caches = this.helper.getClusterCache();
      int size = caches.size();

      for (int i = 0; i < size; i++) {
        MemCachedClient cache = (MemCachedClient)caches.get(i);
        if (getCacheClient(keys[0]).equals(cache))
          continue;
        try
        {
          result = cache.getMulti(keys);
        } catch (Exception ex) {
          Logger.error(
            new StringBuffer(this.helper.getCacheName()).append(" cluster getMulti error"), ex);
        }

        if (result != null) {
          break;
        }
      }
    }
    return result;
  }

  public Object[] getMultiArray(String[] keys) {
    if ((keys == null) || (keys.length <= 0)) {
      return null;
    }
    Object[] result = getCacheClient(keys[0]).getMultiArray(keys);

    if (result != null) {
      return result;
    }
    if (this.helper.hasCluster()) {
      List caches = this.helper.getClusterCache();
      int size = caches.size();

      for (int i = 0; i < size; i++) {
        MemCachedClient cache = (MemCachedClient)caches.get(i);
        if (getCacheClient(keys[0]).equals(cache))
          continue;
        try
        {
          result = cache.getMultiArray(keys);
        } catch (Exception ex) {
          Logger.error(
            new StringBuffer(this.helper.getCacheName()).append(" cluster getMultiArray error"), ex);
        }

        if (result != null)
          break;
      }
    }
    return result;
  }

  public Object put(String key, Object value, Date expiry)
  {
    boolean result = getCacheClient(key).set(key, value, expiry);

    if (result) {
      this.localCache.remove(key);
    }
    if (this.helper.hasCluster()) {
      Object[] commands = { new Integer(1), 
        key, value, expiry };

      addCommandToQueue(commands);
    } else if (!result) {
      throw new RuntimeException(
        "put key :" + key + " error!");
    }
    return value;
  }

  public Object put(String key, Object value, int TTL) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(13, TTL);

    put(key, value, calendar.getTime());

    return value;
  }

  public boolean containsKey(String key) {
    boolean result = false;
    boolean isError = false;
    try
    {
      result = getCacheClient(key).keyExists(key);
    } catch (MemcachedException ex) {
      Logger.error(
        new StringBuffer(this.helper.getCacheName()).append(" cluster containsKey error"), ex);
      isError = true;
    }
    if ((!result) && (this.helper.hasCluster()) && (
      (isError) || (this.helper.getClusterMode().equals("active")))) {
      List caches = this.helper.getClusterCache();
      int size = caches.size();

      for (int i = 0; i < size; i++) {
        MemCachedClient cache = (MemCachedClient)caches.get(i);
        if (getCacheClient(key).equals(cache))
          continue;
        try
        {
          try {
            result = cache.keyExists(key);
          } catch (MemcachedException ex) {
            Logger.error(
              new StringBuffer(this.helper.getCacheName()).append(" cluster containsKey error"), ex);
            continue;
          }

          if ((!this.helper.getClusterMode().equals("active")) || 
            (!result)) break;
          Object[] commands = { new Integer(2), key, cache.get(key) };

          addCommandToQueue(commands);
        }
        catch (Exception e)
        {
          Logger.error(
            new StringBuffer(this.helper.getCacheName()).append(" cluster get error"), e);
        }
      }
    }
    return result;
  }

  public Object get(String key) {
    Object result = null;
    boolean isError = false;
    try
    {
      result = getCacheClient(key).get(key);
    } catch (MemcachedException ex) {
      Logger.error(
        new StringBuffer(this.helper.getCacheName()).append(" cluster get error"), ex);

      isError = true;
    }

    if ((result == null) && (this.helper.hasCluster()) && (
      (isError) || (this.helper.getClusterMode().equals("active")))) {
      List caches = this.helper.getClusterCache();
      int size = caches.size();

      for (int i = 0; i < size; i++) {
        MemCachedClient cache = (MemCachedClient)caches.get(i);
        if (getCacheClient(key).equals(cache))
          continue;
        try
        {
          try {
            result = cache.get(key);
          } catch (MemcachedException ex) {
            Logger.error(
              new StringBuffer(this.helper.getCacheName()).append(" cluster get error"), ex);

            continue;
          }

          if ((!this.helper.getClusterMode().equals("active")) || 
            (result == null)) break;
          Object[] commands = { new Integer(2), key, result };

          addCommandToQueue(commands);
        }
        catch (Exception e)
        {
          Logger.error(
            new StringBuffer(this.helper.getCacheName()).append(" cluster get error"), e);
        }
      }
    }
    return result;
  }

  public Object put(String key, Object value) {
    boolean result = getCacheClient(key).set(key, value);

    if (result) {
      this.localCache.remove(key);
    }
    if (this.helper.hasCluster()) {
      Object[] commands = { new Integer(1), 
        key, value };
      addCommandToQueue(commands);
    } else if (!result) {
      throw new RuntimeException(
        "put key :" + key + " error!");
    }
    return value;
  }

  public void storeCounter(String key, long count) {
    boolean result = getCacheClient(key).storeCounter(key, count);

    if (this.helper.hasCluster()) {
      Object[] commands = { 
        new Integer(3), key, 
        new Long(count) };
      addCommandToQueue(commands);
    } else if (!result) {
      throw new RuntimeException(
        "storeCounter key :" + key + " error!");
    }
  }

  public long getCounter(String key)
  {
    long result = -1L;
    boolean isError = false;
    try
    {
      result = getCacheClient(key).getCounter(key);
    } catch (MemcachedException ex) {
      Logger.error(
        new StringBuffer(this.helper.getCacheName()).append(" cluster getCounter error"), ex);
      isError = true;
    }

    if ((result == -1L) && (this.helper.hasCluster()) && (
      (isError) || (this.helper.getClusterMode().equals("active")))) {
      List caches = this.helper.getClusterCache();
      int size = caches.size();

      for (int i = 0; i < size; i++) {
        MemCachedClient cache = (MemCachedClient)caches.get(i);
        if (getCacheClient(key).equals(cache))
          continue;
        try
        {
          try {
            result = cache.getCounter(key);
          } catch (MemcachedException ex) {
            Logger.error(
              new StringBuffer(this.helper.getCacheName()).append(" cluster getCounter error"), ex);
            continue;
          }

          if ((result == -1L) || 
            (!this.helper.getClusterMode()
            .equals("active"))) break;
          Object[] commands = { 
            new Integer(4), 
            key, new Long(result) };

          addCommandToQueue(commands);
        }
        catch (Exception e)
        {
          Logger.error(
            new StringBuffer(this.helper.getCacheName()).append(" cluster getCounter error"), e);
        }
      }
    }

    return result;
  }

  public long addOrDecr(String key, long decr) {
    long result = getCacheClient(key).addOrDecr(key, decr);

    if (this.helper.hasCluster()) {
      Object[] commands = { 
        new Integer(5), key, new Long(decr) };

      addCommandToQueue(commands);
    }

    return result;
  }

  public long addOrIncr(String key, long inc) {
    long result = getCacheClient(key).addOrIncr(key, inc);

    if (this.helper.hasCluster()) {
      Object[] commands = { 
        new Integer(6), key, new Long(inc) };

      addCommandToQueue(commands);
    }

    return result;
  }

  public long decr(String key, long decr) {
    long result = getCacheClient(key).decr(key, decr);

    if (this.helper.hasCluster()) {
      Object[] commands = { new Integer(7), 
        key, new Long(decr) };

      addCommandToQueue(commands);
    }

    return result;
  }

  public long incr(String key, long inc) {
    long result = getCacheClient(key).incr(key, inc);

    if (this.helper.hasCluster()) {
      Object[] commands = { new Integer(8), 
        key, new Long(inc) };

      addCommandToQueue(commands);
    }

    return result;
  }

  public Object remove(String key) {
    Object result = new Boolean(getCacheClient(key).delete(key));

    if (this.helper.hasCluster()) {
      List caches = this.helper.getClusterCache();
      int size = caches.size();

      for (int i = 0; i < size; i++) {
        MemCachedClient cache = (MemCachedClient)caches.get(i);
        if (getCacheClient(key).equals(cache))
          continue;
        try
        {
          cache.delete(key);
        } catch (Exception ex) {
          Logger.error(
            new StringBuffer(this.helper.getCacheName()).append(" cluster remove error"), ex);
        }
      }
    }
    return result;
  }

  public int size() {
    throw new UnsupportedOperationException(
      "Memcached not support size method!");
  }

  public Collection values() {
    Set values = new HashSet();
    Map dumps = new HashMap();

    Map slabs = this.helper.getInnerCacheClient().statsItems();

    if ((slabs != null) && (slabs.keySet() != null)) {
      Iterator itemsItr = slabs.keySet().iterator();

      while (itemsItr.hasNext()) {
        String server = itemsItr.next().toString();
        Map cdxMcs = (Map)slabs.get(server);
        Iterator cdxMcItr = cdxMcs.keySet().iterator();

        while (cdxMcItr.hasNext()) {
          String cdxMc = cdxMcItr.next().toString();

          String[] itemAtt = cdxMc.split(":");

          if (itemAtt[2].startsWith("number")) {
            dumps.put(itemAtt[1], Integer.valueOf(itemAtt[1]));
          }
        }
      }
      if (!dumps.values().isEmpty()) {
        Iterator dumpIter = dumps.values().iterator();

        while (dumpIter.hasNext()) {
          int dump = ((Integer)dumpIter.next()).intValue();

          Map cacheDump = this.helper.getInnerCacheClient()
            .statsCacheDump(dump, 50000);

          Iterator entryIter = cacheDump.values().iterator();

          while (entryIter.hasNext()) {
            Map items = (Map)entryIter.next();

            Iterator ks = items.keySet().iterator();

            while (ks.hasNext()) {
              String k = (String)ks.next();
              try
              {
                k = URLDecoder.decode(k, "UTF-8");
              } catch (Exception ex) {
                Logger.error(ex);
              }

              if ((k != null) && (!k.trim().equals(""))) {
                Object value = get(k);

                if (value != null) {
                  values.add(value);
                }
              }
            }
          }
        }
      }
    }

    return values;
  }

  public Set keySet(boolean fast) {
    Set keys = new HashSet();
    Map dumps = new HashMap();

    Map slabs = this.helper.getInnerCacheClient().statsItems();

    if ((slabs != null) && (slabs.keySet() != null)) {
      Iterator itemsItr = slabs.keySet().iterator();

      while (itemsItr.hasNext()) {
        String server = itemsItr.next().toString();
        Map cdxMcs = (Map)slabs.get(server);
        Iterator cdxMcItr = cdxMcs.keySet().iterator();

        while (cdxMcItr.hasNext()) {
          String cdxMc = cdxMcItr.next().toString();

          String[] itemAtt = cdxMc.split(":");

          if (itemAtt[2].startsWith("number")) {
            dumps.put(itemAtt[1], Integer.valueOf(itemAtt[1]));
          }
        }
      }
      if (!dumps.values().isEmpty()) {
        Iterator dumpIter = dumps.values().iterator();

        while (dumpIter.hasNext()) {
          int dump = ((Integer)dumpIter.next()).intValue();

          Map cacheDump = this.helper.getInnerCacheClient()
            .statsCacheDump(dump, 0);

          Iterator entryIter = cacheDump.values().iterator();

          while (entryIter.hasNext()) {
            Map items = (Map)entryIter.next();

            Iterator ks = items.keySet().iterator();

            while (ks.hasNext()) {
              String k = (String)ks.next();
              try
              {
                k = URLDecoder.decode(k, "UTF-8");
              } catch (Exception ex) {
                Logger.error(ex);
              }

              if ((k != null) && (!k.trim().equals(""))) {
                if (fast)
                  keys.add(k);
                else if (containsKey(k))
                  keys.add(k);
              }
            }
          }
        }
      }
    }
    return keys;
  }

  public MemCachedClient getCacheClient(Object key) {
    if (this.helper == null) {
      Logger.error("MemcachedCache helper is null!");
      throw new RuntimeException("MemcachedCache helper is null!");
    }
    return this.helper.getCacheClient(key);
  }

  public MemCachedClientHelper getHelper() {
    return this.helper;
  }

  public void setHelper(MemCachedClientHelper helper) {
    this.helper = helper;
  }

  public Set keySet() {
    return keySet(false);
  }

  public Object get(String key, int localTTL) {
    Object result = null;

    result = this.localCache.get(key);

    if (result == null) {
      result = get(key);

      if (result != null) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(13, localTTL);
        this.localCache.put(key, result, calendar.getTime());
      }
    }

    return result;
  }

  public MemcacheStats[] stats() {
    MemcacheStats[] result = (MemcacheStats[])null;

    Map statMap = this.helper.getInnerCacheClient().stats();

    if ((statMap != null) && (!statMap.isEmpty())) {
      result = new MemcacheStats[statMap.size()];

      Iterator iter = statMap.keySet().iterator();

      int i = 0;

      while (iter.hasNext()) {
        result[i] = new MemcacheStats();
        result[i].setServerHost((String)iter.next());
        result[i].setStatInfo(
          statMap.get(result[i].getServerHost()).toString());
        i++;
      }
    }

    return result;
  }

  public MemcacheStatsSlab[] statsSlabs() {
    MemcacheStatsSlab[] result = (MemcacheStatsSlab[])null;

    Map statMap = this.helper.getInnerCacheClient().statsSlabs();

    if ((statMap != null) && (!statMap.isEmpty())) {
      result = new MemcacheStatsSlab[statMap.size()];

      Iterator iter = statMap.keySet().iterator();

      int i = 0;

      while (iter.hasNext()) {
        result[i] = new MemcacheStatsSlab();
        result[i].setServerHost((String)iter.next());

        Map node = (Map)statMap.get(result[i].getServerHost());

        Iterator nodeIter = node.keySet().iterator();

        while (nodeIter.hasNext()) {
          String key = (String)nodeIter.next();
          result[i].addSlab(key, node.get(key).toString());
        }
        i++;
      }
    }

    return result;
  }

  public Map statsItems() {
    Map items = this.helper.getInnerCacheClient().statsItems();
    return items;
  }

  public void addCommandToQueue(Object[] command)
  {
    this.dataQueue.add(command);
  }

  public void destroy() {
    try {
      if (this.localCache != null) {
        this.localCache.destroy();
      }
      if (this.processor != null) {
        this.processor.stopProcess();
      }
      if (this.task != null)
        this.task.stopTask();
    }
    catch (Exception ex)
    {
      Logger.error(ex);
    }
  }

  public MemcachedResponse statCacheResponse() {
    if (this.localCache.get("cacheStatusResponse") == null) {
      MemcachedResponse response = new MemcachedResponse();
      response.setCacheName(this.helper.getCacheName());
      this.localCache.put("cacheStatusResponse", response);
    }

    return (MemcachedResponse)this.localCache.get("cacheStatusResponse");
  }

  public long getStatisticsInterval() {
    return this.statisticsInterval;
  }

  public void setStatisticsInterval(long statisticsInterval) {
    this.statisticsInterval = statisticsInterval;
  }

  public boolean add(String key, Object value) {
    boolean result = getCacheClient(key).add(key, value);

    if (this.helper.hasCluster()) {
      Object[] commands = { new Integer(9), 
        key, value };

      addCommandToQueue(commands);
    }

    return result;
  }

  public boolean add(String key, Object value, Date expiry) {
    boolean result = getCacheClient(key).add(key, value, expiry);

    if (this.helper.hasCluster()) {
      Object[] commands = { new Integer(9), 
        key, value, expiry };

      addCommandToQueue(commands);
    }

    return result;
  }

  public boolean replace(String key, Object value) {
    boolean result = getCacheClient(key).replace(key, value);

    if (this.helper.hasCluster()) {
      Object[] commands = { 
        new Integer(10), key, value };

      addCommandToQueue(commands);
    }

    return result;
  }

  public boolean replace(String key, Object value, Date expiry) {
    boolean result = getCacheClient(key).replace(key, value, expiry);

    if (this.helper.hasCluster()) {
      Object[] commands = { 
        new Integer(10), key, value, expiry };

      addCommandToQueue(commands);
    }

    return result;
  }

  public void asynPut(String key, Object value)
  {
    Object[] commands = { new Integer(11), 
      key, value };

    addCommandToQueue(commands);
  }

  public void asynAddOrDecr(String key, long decr) {
    Object[] commands = { 
      new Integer(13), key, new Long(decr) };

    addCommandToQueue(commands);
  }

  public void asynAddOrIncr(String key, long incr) {
    Object[] commands = { 
      new Integer(14), key, new Long(incr) };

    addCommandToQueue(commands);
  }

  public void asynDecr(String key, long decr) {
    Object[] commands = { new Integer(15), 
      key, new Long(decr) };

    addCommandToQueue(commands);
  }

  public void asynIncr(String key, long incr)
  {
    Object[] commands = { new Integer(16), 
      key, new Long(incr) };

    addCommandToQueue(commands);
  }

  public void asynStoreCounter(String key, long count) {
    Object[] commands = { 
      new Integer(12), key, 
      new Long(count) };

    addCommandToQueue(commands);
  }

  class StatisticsTask extends Thread
  {
    private boolean flag = true;

    StatisticsTask() {  }

    public void run() { while (this.flag) {
        long consume = 0L;
        try
        {
          Thread.sleep(MemcachedCache.this.statisticsInterval * 1000L);

          consume = checkResponse();
        } catch (InterruptedException e) {
          MemcachedCache.Logger.warn("StatisticsTask stoped!");
        } catch (Exception ex) {
          MemcachedCache.Logger.error("StatisticsTask execute error", ex);
          consume = -1L;
        }

        if (MemcachedCache.this.localCache != null) {
          MemcachedResponse response = (MemcachedResponse)MemcachedCache.this.localCache
            .get("cacheStatusResponse");

          if ((response != null) && (response.getResponses() != null))
            response.getResponses().add(new Long(consume));
        }
      }
    }

    private long checkResponse()
    {
      if (MemcachedCache.this.localCache.get("cacheStatusResponse") == null) {
        MemcachedResponse response = new MemcachedResponse();
        response.setCacheName(MemcachedCache.this.helper.getCacheName());
        MemcachedCache.this.localCache.put("cacheStatusResponse", response);
      }
      else if (((MemcachedResponse)MemcachedCache.this.localCache.get("cacheStatusResponse"))
        .getEndTime().before(new Date())) {
        ((MemcachedResponse)MemcachedCache.this.localCache.get("cacheStatusResponse")).ini();
      }

      long consume = System.currentTimeMillis();

      MemcachedCache.this.put("cacheStatusResponse", "cacheStatusResponse");
      MemcachedCache.this.get("cacheStatusResponse");

      consume = System.currentTimeMillis() - consume;

      return consume;
    }

    public void stopTask() {
      this.flag = false;
      interrupt();
    }

    public boolean isFlag() {
      return this.flag;
    }

    public void setFlag(boolean flag) {
      this.flag = flag;
    }
  }
}
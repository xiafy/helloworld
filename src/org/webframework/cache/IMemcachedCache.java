package org.webframework.cache;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.webframework.cache.memcached.MemcacheStats;
import org.webframework.cache.memcached.MemcacheStatsSlab;
import org.webframework.cache.memcached.MemcachedResponse;

public abstract interface IMemcachedCache extends ICache
{
  public abstract Object get(String paramString, int paramInt);

  public abstract Object[] getMultiArray(String[] paramArrayOfString);

  public abstract Map getMulti(String[] paramArrayOfString);

  public abstract long incr(String paramString, long paramLong);

  public abstract long decr(String paramString, long paramLong);

  public abstract long addOrIncr(String paramString, long paramLong);

  public abstract long addOrDecr(String paramString, long paramLong);

  public abstract void storeCounter(String paramString, long paramLong);

  public abstract long getCounter(String paramString);

  public abstract Set keySet(boolean paramBoolean);

  public abstract MemcacheStatsSlab[] statsSlabs();

  public abstract MemcacheStats[] stats();

  public abstract Map statsItems();

  public abstract MemcachedResponse statCacheResponse();

  public abstract void setStatisticsInterval(long paramLong);

  public abstract boolean add(String paramString, Object paramObject);

  public abstract boolean add(String paramString, Object paramObject, Date paramDate);

  public abstract boolean replace(String paramString, Object paramObject);

  public abstract boolean replace(String paramString, Object paramObject, Date paramDate);

  public abstract void asynPut(String paramString, Object paramObject);

  public abstract void asynAddOrDecr(String paramString, long paramLong);

  public abstract void asynAddOrIncr(String paramString, long paramLong);

  public abstract void asynDecr(String paramString, long paramLong);

  public abstract void asynIncr(String paramString, long paramLong);

  public abstract void asynStoreCounter(String paramString, long paramLong);
}
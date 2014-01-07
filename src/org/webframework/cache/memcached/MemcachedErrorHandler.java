package org.webframework.cache.memcached;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.cache.memcached.client.ErrorHandler;
import org.webframework.cache.memcached.client.MemCachedClient;

public class MemcachedErrorHandler
  implements ErrorHandler
{
  private static final Log Logger = LogFactory.getLog(MemcachedErrorHandler.class);

  public void handleErrorOnDelete(MemCachedClient client, Throwable error, String cacheKey)
  {
    Logger.error(
      "ErrorOnDelete, cacheKey: " + cacheKey, 
      error);
  }

  public void handleErrorOnFlush(MemCachedClient client, Throwable error) {
    Logger.error("ErrorOnFlush", error);
  }

  public void handleErrorOnGet(MemCachedClient client, Throwable error, String cacheKey)
  {
    Logger.error(
      "ErrorOnGet, cacheKey: " + cacheKey, error);
  }

  public void handleErrorOnGet(MemCachedClient client, Throwable error, String[] cacheKeys)
  {
    Logger.error(
      "ErrorOnGet, cacheKey: " + cacheKeys, error);
  }

  public void handleErrorOnInit(MemCachedClient client, Throwable error) {
    Logger.error("ErrorOnInit", error);
  }

  public void handleErrorOnSet(MemCachedClient client, Throwable error, String cacheKey)
  {
    Logger.error(
      "ErrorOnSet, cacheKey: " + cacheKey, error);
  }

  public void handleErrorOnStats(MemCachedClient client, Throwable error) {
    Logger.error("ErrorOnStats", error);
  }
}
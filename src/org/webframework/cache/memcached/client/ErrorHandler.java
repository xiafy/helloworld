package org.webframework.cache.memcached.client;

public abstract interface ErrorHandler
{
  public abstract void handleErrorOnInit(MemCachedClient paramMemCachedClient, Throwable paramThrowable);

  public abstract void handleErrorOnGet(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String paramString);

  public abstract void handleErrorOnGet(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String[] paramArrayOfString);

  public abstract void handleErrorOnSet(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String paramString);

  public abstract void handleErrorOnDelete(MemCachedClient paramMemCachedClient, Throwable paramThrowable, String paramString);

  public abstract void handleErrorOnFlush(MemCachedClient paramMemCachedClient, Throwable paramThrowable);

  public abstract void handleErrorOnStats(MemCachedClient paramMemCachedClient, Throwable paramThrowable);
}
package org.webframework.cache.memcached;

public class MemcachedException extends RuntimeException
{
  public MemcachedException()
  {
  }

  public MemcachedException(String message)
  {
    super(message);
  }

  public MemcachedException(String message, Throwable cause) {
    super(message, cause);
  }

  public MemcachedException(Throwable cause) {
    super(cause);
  }
}
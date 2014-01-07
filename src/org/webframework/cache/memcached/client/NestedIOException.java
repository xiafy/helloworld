package org.webframework.cache.memcached.client;

import java.io.IOException;

public class NestedIOException extends IOException
{
  private static final long serialVersionUID = 1L;

  public NestedIOException(Throwable cause)
  {
    super(cause.getMessage());
    super.initCause(cause);
  }

  public NestedIOException(String message, Throwable cause) {
    super(message);
    initCause(cause);
  }
}
package org.webframework.cache.memcached.client;

import java.io.IOException;

public abstract interface LineInputStream
{
  public abstract String readLine()
    throws IOException;

  public abstract void clearEOL()
    throws IOException;

  public abstract int read(byte[] paramArrayOfByte)
    throws IOException;
}
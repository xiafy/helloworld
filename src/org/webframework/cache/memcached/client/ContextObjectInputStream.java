package org.webframework.cache.memcached.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ContextObjectInputStream extends ObjectInputStream
{
  ClassLoader mLoader;

  public ContextObjectInputStream(InputStream in, ClassLoader loader)
    throws IOException, SecurityException
  {
    super(in);
    this.mLoader = loader;
  }

  protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException
  {
    if (this.mLoader == null) {
      return super.resolveClass(v);
    }
    return Class.forName(v.getName(), true, this.mLoader);
  }
}
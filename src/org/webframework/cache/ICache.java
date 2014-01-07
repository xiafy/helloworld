package org.webframework.cache;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

public abstract interface ICache
{
  public abstract Object put(String paramString, Object paramObject);

  public abstract Object put(String paramString, Object paramObject, Date paramDate);

  public abstract Object put(String paramString, Object paramObject, int paramInt);

  public abstract Object get(String paramString);

  public abstract Object remove(String paramString);

  public abstract boolean clear();

  public abstract int size();

  public abstract Set keySet();

  public abstract Collection values();

  public abstract boolean containsKey(String paramString);

  public abstract void destroy();
}
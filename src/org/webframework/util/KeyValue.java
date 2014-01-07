package org.webframework.util;

public class KeyValue
{
  private Object key;
  private Object value;

  public KeyValue()
  {
  }

  public KeyValue(Object key, Object value)
  {
    this.key = key;
    this.value = value;
  }

  public Object getKey()
  {
    return this.key;
  }

  public Object getValue()
  {
    return this.value;
  }

  public void setKey(Object object)
  {
    this.key = object;
  }

  public void setValue(Object object)
  {
    this.value = object;
  }
}
package org.webframework.bean.beans;

import org.springframework.beans.PropertyValue;

public abstract interface PropertyValues
{
  public abstract PropertyValue[] getPropertyValues();

  public abstract PropertyValue getPropertyValue(String paramString);

  public abstract boolean contains(String paramString);

  public abstract PropertyValues changesSince(PropertyValues paramPropertyValues);
}
package org.webframework.bean;

import java.util.Map;

public abstract interface EnumService
{
  public abstract String getDescByValue(String paramString1, String paramString2);

  public abstract String[] getValues(String paramString);

  public abstract String[] getDescs(String paramString);

  public abstract Map getValueDescs(String paramString);

  public abstract boolean contains(String paramString);
}
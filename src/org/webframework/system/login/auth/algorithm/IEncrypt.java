package org.webframework.system.login.auth.algorithm;

public abstract interface IEncrypt
{
  public abstract String encode(String paramString)
    throws Exception;

  public abstract String decode(String paramString)
    throws Exception;
}
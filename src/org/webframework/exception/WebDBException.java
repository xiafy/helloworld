package org.webframework.exception;

public class WebDBException extends WebException
{
  public WebDBException(String s)
  {
    super(s);
  }

  public WebDBException(String s, Exception e) {
    super(s, e);
  }
}
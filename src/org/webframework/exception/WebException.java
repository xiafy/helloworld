package org.webframework.exception;

public class WebException extends Exception
{
  public WebException(String s)
  {
    super(s);
  }

  public WebException(String s, Exception e) {
    super(s, e);
  }
}
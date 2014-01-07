package org.webframework.exception;

public class WebTagException extends WebException
{
  public WebTagException(String s)
  {
    super(s);
  }

  public WebTagException(String s, Exception e) {
    super(s, e);
  }
}
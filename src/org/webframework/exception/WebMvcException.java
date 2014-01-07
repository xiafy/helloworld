package org.webframework.exception;

public class WebMvcException extends WebException
{
  public WebMvcException(String s)
  {
    super(s);
  }

  public WebMvcException(String s, Exception e) {
    super(s, e);
  }
}
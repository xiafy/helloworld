package org.webframework.exception;

public class WebRuntimeException extends RuntimeException
{
  Throwable nest = this;

  public WebRuntimeException()
  {
  }

  public WebRuntimeException(String message)
  {
    super(message);
  }

  public WebRuntimeException(String message, Throwable t)
  {
    super(message, t);
    this.nest = t;
  }

  public WebRuntimeException(Throwable t)
  {
    this.nest = t;
  }
}
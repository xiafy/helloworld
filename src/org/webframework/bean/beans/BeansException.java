package org.webframework.bean.beans;

import org.springframework.core.NestedRuntimeException;

public abstract class BeansException extends NestedRuntimeException
{
  public BeansException(String msg)
  {
    super(msg);
  }

  public BeansException(String msg, Throwable t)
  {
    super(msg, t);
  }
}
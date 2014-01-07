package org.webframework.bean.beans.factory;

import org.springframework.beans.FatalBeanException;

public class BeanDefinitionStoreException extends FatalBeanException
{
  public BeanDefinitionStoreException(String msg)
  {
    super(msg);
  }

  public BeanDefinitionStoreException(String msg, Throwable t) {
    super(msg, t);
  }
}
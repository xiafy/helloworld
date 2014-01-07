package org.webframework.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.AbstractXmlApplicationContext;

public abstract interface BeanConfig extends InitializingBean
{
  public abstract void loadBeanConfig(AbstractXmlApplicationContext paramAbstractXmlApplicationContext);
}
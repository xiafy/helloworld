package org.webframework.bean.context.support;

import java.io.File;
import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.core.io.Resource;

public class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext
{
  private boolean validating = true;

  public AbstractXmlApplicationContext() {
  }
  public AbstractXmlApplicationContext(ApplicationContext parent) { super(parent); }

  public boolean isValidating()
  {
    return this.validating;
  }
  public void setValidating(boolean validating) {
    this.validating = validating;
  }
  public String getResourceBasePath() {
    return new File("").getAbsolutePath() + File.separatorChar;
  }

  protected void loadBeanDefinitions(DefaultListableBeanFactory arg0) throws BeansException, IOException {
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(arg0);

    beanDefinitionReader.setResourceLoader(this);
    beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

    initBeanDefinitionReader(beanDefinitionReader);
    loadBeanDefinitions(beanDefinitionReader);
  }

  protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
    reader.setValidating(this.validating);
  }
  protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
    Resource[] configResources = getConfigResources();
    if (configResources != null) {
      reader.loadBeanDefinitions(configResources);
    }
    String[] configLocations = getConfigLocations();
    if (configLocations != null)
      reader.loadBeanDefinitions(configLocations);
  }

  protected Resource[] getConfigResources() {
    return null;
  }
}
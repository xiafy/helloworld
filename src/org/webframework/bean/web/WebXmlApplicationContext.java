package org.webframework.bean.web;

import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class WebXmlApplicationContext extends FileSystemXmlApplicationContext
{
  private String contextBasePath;

  public WebXmlApplicationContext(String configLocation)
    throws BeansException
  {
    super(configLocation);
  }

  public WebXmlApplicationContext(String[] configLocations) throws BeansException
  {
    super(configLocations);
  }

  public String getResourceBasePath() {
    return this.contextBasePath;
  }

  public void setResourceBasePath(String contextBasePath) {
    this.contextBasePath = contextBasePath;
  }
}
package org.webframework;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.webframework.bean.EnumService;

public class EnumConfig
  implements EnumService, InitializingBean, ApplicationContextAware
{
  public boolean contains(String enumName)
  {
    return false;
  }

  public String getDescByValue(String enumName, String value)
  {
    return null;
  }

  public String[] getDescs(String enumName)
  {
    return null;
  }

  public Map getValueDescs(String enumName)
  {
    return null;
  }

  public String[] getValues(String enumName)
  {
    return null;
  }

  public void afterPropertiesSet()
    throws Exception
  {
  }

  public void setApplicationContext(ApplicationContext arg0)
    throws BeansException
  {
  }
}
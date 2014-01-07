package org.webframework;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BeanContextLoaderListener
  implements ServletContextListener
{
  public static final String KEY_CONF = "beanConfig";
  private ServletContext context;

  public void contextInitialized(ServletContextEvent event)
  {
    this.context = event.getServletContext();
    String config = this.context.getInitParameter("beanConfig");
    BeanFactory.initApplicationContextInWebApp(this.context, config);
  }

  public void contextDestroyed(ServletContextEvent event)
  {
    BeanFactory.destoryApplicationContext(this.context);
  }
}
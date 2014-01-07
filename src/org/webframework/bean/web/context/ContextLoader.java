package org.webframework.bean.web.context;

import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ContextLoader
{
  public static final String CONTEXT_CLASS_PARAM = "contextClass";
  public static final Class DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

  private static final Log logger = LogFactory.getLog(ContextLoader.class);

  public static WebApplicationContext initContext(ServletContext servletContext)
    throws BeansException
  {
    servletContext.log("Loading root WebApplicationContext");
    String contextClass = servletContext.getInitParameter("contextClass");
    try
    {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      Class clazz = contextClass != null ? Class.forName(contextClass, true, cl) : DEFAULT_CONTEXT_CLASS;
      logger.debug("Loading root WebApplicationContext: using context class [" + clazz.getName() + "]");
      if (!WebApplicationContext.class.isAssignableFrom(clazz)) {
        throw new ApplicationContextException("Context class [" + contextClass + "] is no WebApplicationContext");
      }
      WebApplicationContext webApplicationContext = (WebApplicationContext)clazz.newInstance();
      webApplicationContext.setServletContext(servletContext);
      return webApplicationContext;
    }
    catch (BeansException ex) {
      handleException("Failed to initialize application context", ex);
    }
    catch (ClassNotFoundException ex) {
      handleException("Failed to load config class [" + contextClass + "]", ex);
    }
    catch (InstantiationException ex) {
      handleException("Failed to instantiate config class [" + contextClass + 
        "]: does it have a public no arg constructor?", ex);
    }
    catch (IllegalAccessException ex) {
      handleException("Illegal access while finding or instantiating config class [" + 
        contextClass + "]: does it have a public no arg constructor?", ex);
    }
    catch (Throwable ex) {
      handleException("Unexpected error loading context configuration", ex);
    }

    return null;
  }

  private static void handleException(String msg, Throwable ex)
    throws BeansException
  {
    logger.error(msg, ex);
    if ((ex instanceof Error)) {
      throw ((Error)ex);
    }
    if ((ex instanceof BeansException)) {
      throw ((BeansException)ex);
    }

    throw new ApplicationContextException(msg, ex);
  }

  public static void closeContext(ServletContext servletContext)
    throws ApplicationContextException
  {
    servletContext.log("Closing root WebApplicationContext");
    WebApplicationContext wac = (WebApplicationContext)WebApplicationContextUtils.getWebApplicationContext(servletContext);
    if ((wac instanceof ConfigurableApplicationContext))
      ((ConfigurableApplicationContext)wac).close();
  }
}
package org.webframework;

import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.webframework.bean.BeanConfig;
import org.webframework.bean.web.WebXmlApplicationContext;
import org.webframework.bean.web.context.ContextLoader;

public class BeanFactory
{
  private static String FILE_SEPARATOR = System.getProperty("file.separator");
  private static Log log = LogFactory.getLog(BeanFactory.class);
  private static boolean initialized = false;
  private static ApplicationContext applicationContext = null;

  public static ApplicationContext getApplicationContext() { return applicationContext; }

  private static String transform(String s) {
    if (FILE_SEPARATOR.equals("\\"))
      return s.replace('/', '\\');
    if (FILE_SEPARATOR.equals("/")) {
      return s.replace('\\', '/');
    }
    return s;
  }

  public static Object getBean(String id)
  {
    if (!initialized) {
      throw new RuntimeException("ComponentFactory没有初始化，请检查是否加载了此配置");
    }
    return applicationContext.getBean(id);
  }

  public static synchronized void initApplicationContextInWebContext(ServletContext sc)
  {
    if (initialized) {
      return;
    }
    log.info("从应用上下文中加载配置 ");
    applicationContext = 
      WebApplicationContextUtils.getRequiredWebApplicationContext(sc);

    initialized = true;
    log.info("配置加载完成.");
  }

  public static synchronized void initApplicationContextInWebApp(ServletContext sc, String configs)
  {
    if (initialized) {
      log.error("配置已经加载，跳过");
      return;
    }
    if (configs == null) {
      log.error("配置为空，跳过");
      return;
    }

    String[] xmls = StringUtils.tokenizeToStringArray(configs, ",", true, false);

    String[] configs1 = new String[7];
    configs1[0] = transform("/" + sc.getRealPath("/") + "/WEB-INF/conf/jdbcSupport.xml");
    configs1[1] = transform("/" + sc.getRealPath("/") + "/WEB-INF/conf/dao.xml");
    configs1[2] = transform("/" + sc.getRealPath("/") + "/WEB-INF/conf/mvc.xml");
    configs1[3] = transform("/" + sc.getRealPath("/") + "/WEB-INF/conf/sys.xml");
    configs1[4] = transform("/" + sc.getRealPath("/") + "/WEB-INF/conf/cache.xml");
    configs1[5] = transform("/" + sc.getRealPath("/") + "/WEB-INF/conf/commonCollectionSupport.xml");
    configs1[6] = transform("/" + sc.getRealPath("/") + "/WEB-INF/conf/beanContext.xml");

    applicationContext = new WebXmlApplicationContext(configs1);
    ((WebXmlApplicationContext)applicationContext).setResourceBasePath(sc.getRealPath("/"));
    org.webframework.bean.EnumConfig.contextBasePath = sc.getRealPath("/");

    initialized = true;
    log.info("配置加载完成 [" + configs + "].");
  }

  public static synchronized void initApplicationContext(String realPath)
  {
    initApplicationContextInClassPath(new String[] { realPath });
  }

  public static synchronized void initApplicationContext(String[] realPaths) {
    if (initialized) {
      return;
    }
    log.info("loading ApplicationContext from class path...");
    applicationContext = new FileSystemXmlApplicationContext(realPaths);
    initIncluded();
    initialized = true;
    log.info("ApplicationContext loaded.");
  }

  public static synchronized void initApplicationContextInClassPath(String config)
  {
    initApplicationContextInClassPath(new String[] { config });
  }

  public static synchronized void initApplicationContextInClassPath(String[] configs) {
    if (initialized) {
      return;
    }
    log.info("loading ApplicationContext from class path...");
    applicationContext = new ClassPathXmlApplicationContext(configs);
    initIncluded();
    initialized = true;
    log.info("ApplicationContext loaded.");
  }

  private static void initIncluded()
  {
    if ((applicationContext instanceof AbstractXmlApplicationContext)) {
      String[] map = applicationContext.getBeanDefinitionNames();
      for (int i = 0; i < map.length; i++) {
        Object o = applicationContext.getBean(map[i]);
        if (!(o instanceof BeanConfig)) continue;
        try {
          BeanConfig cfg = (BeanConfig)o;
          cfg.loadBeanConfig((AbstractXmlApplicationContext)applicationContext);
        } catch (Throwable e) {
          log.error("load included configuration error", e);
        }
      }
    }
  }

  private static void initIncludedto()
  {
    if ((applicationContext instanceof AbstractXmlApplicationContext)) {
      String[] map = applicationContext.getBeanDefinitionNames();
      for (int i = 0; i < map.length; i++) {
        Object o = applicationContext.getBean(map[i]);
        if (!(o instanceof BeanConfig)) continue;
        try {
          BeanConfig cfg = (BeanConfig)o;
          cfg.loadBeanConfig((AbstractXmlApplicationContext)applicationContext);
        } catch (Throwable e) {
          log.error("load included configuration error", e);
        }
      }
    }
  }

  public static synchronized void destoryApplicationContext(ServletContext sc)
  {
    if (!initialized) {
      return;
    }
    ContextLoader.closeContext(sc);
    applicationContext = null;
    initialized = false;
  }
}
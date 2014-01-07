package org.webframework.cache.platform;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.BeanFactory;
import org.webframework.cache.ICacheManager;
import org.webframework.cache.IMemcachedCache;
import org.webframework.cache.memcached.CacheUtil;
import org.webframework.cache.memcached.MemcachedCacheManager;

public class CacheInitListener
  implements ServletContextListener
{
  private static final Log Logger = LogFactory.getLog(CacheInitListener.class);
  private ICacheManager manager;
  private CacheConfig cacheConfig;
  private URL url;

  public void contextInitialized(ServletContextEvent event)
  {
    loadConfig(event);
    if (this.cacheConfig.isOn()) {
      this.manager = 
        CacheUtil.getCacheManager(IMemcachedCache.class, 
        MemcachedCacheManager.class.getName());
      this.manager.setConfigFileUrl(this.url);
      this.manager.start();
    }
    Cache.getInstance().setOn(this.cacheConfig.isOn());
    Cache.getInstance().setCacheManager(this.manager);
  }

  private void loadConfig(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    this.cacheConfig = ((CacheConfig)BeanFactory.getBean("cacheConfig"));
    if (this.cacheConfig.isOn()) {
      String path = this.cacheConfig.getConfigPath();
      if ((path == null) || ("".equals(path.trim()))) {
        this.cacheConfig.setConfigPath("/WEB-INF/conf/memcached.xml");
      }
      path = this.cacheConfig.getConfigPath();
      try {
        if (path.startsWith("http"))
          this.url = new URL(path);
        else
          this.url = context.getResource(path);
      }
      catch (MalformedURLException e) {
        e.printStackTrace();
        Logger.error("读取cache配置文件失败,请检查配置文件地址");
        this.cacheConfig.setOn(false);
      }
    }
  }

  public void contextDestroyed(ServletContextEvent event)
  {
    if (this.manager != null)
      this.manager.stop();
  }
}
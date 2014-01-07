package org.webframework.system;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.system.log.track.core.LogThreadPool;
import org.webframework.system.manage.cache.MenuCacheService;
import org.webframework.system.manage.cache.OrganCacheService;

public class SysListener
  implements ServletContextListener
{
  Log log = LogFactory.getLog(SysListener.class);

  public void contextInitialized(ServletContextEvent event) {
    startCache();
    startLog();
  }
  public void contextDestroyed(ServletContextEvent event) {
    shutdownLog();
  }

  private void startCache()
  {
    try {
      new MenuCacheService().cacheMenu();
    } catch (Exception e) {
      this.log.error("系统启动时缓存菜单出错!");
      e.printStackTrace();
    }
    try
    {
      new OrganCacheService().cacheOrgan();
    } catch (Exception e) {
      this.log.error("系统启动时缓存组织机构出错!");
      e.printStackTrace();
    }
    try
    {
      SysContext.refreshSystemConfig();
    } catch (Exception e) {
      this.log.error("系统启动时缓存系统配置出错!");
      e.printStackTrace();
    }
  }

  private void startLog() {
    LogThreadPool.start();
  }

  private void shutdownLog() {
    LogThreadPool.shutdown(true);
  }
}
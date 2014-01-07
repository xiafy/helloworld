package org.webframework.system.log.track.core;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogThreadPool
{
  private static Log log = LogFactory.getLog(LogThreadPool.class);

  private static ExecutorService service = Executors.newCachedThreadPool();

  private static MaintThread maintThread = null;

  public static void start()
  {
    if (maintThread != null) {
      if (maintThread.isRunning())
        log.error("日志任务调配线程已经正在运行中!");
      else
        maintThread.start();
    }
    else {
      maintThread = new MaintThread(service);
      maintThread.start();
    }
  }

  public static void shutdown() {
    if ((maintThread != null) && (maintThread.isRunning()))
      maintThread.stopThread();
  }

  public static void shutdown(boolean shutdownPool)
  {
    shutdown();
    if (shutdownPool)
    {
      service.shutdown();
      try
      {
        if (!service.awaitTermination(60L, TimeUnit.SECONDS)) {
          service.shutdownNow();

          if (!service.awaitTermination(60L, TimeUnit.SECONDS))
            log.error("日志线程池 没有关闭成功!");
        }
      }
      catch (InterruptedException ie)
      {
        service.shutdownNow();

        Thread.currentThread().interrupt();
      }
    }
  }
}
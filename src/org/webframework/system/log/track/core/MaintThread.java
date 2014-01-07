package org.webframework.system.log.track.core;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.system.log.track.LogUtils;

public class MaintThread extends Thread
{
  private static Log log = LogFactory.getLog(MaintThread.class);
  private ExecutorService service;
  private boolean stopThread = false;
  private boolean running;

  public MaintThread(ExecutorService service)
  {
    this.service = service;
  }

  public boolean isRunning() {
    return this.running;
  }

  public void stopThread() {
    this.stopThread = true;
    interrupt();
  }

  public void run() {
    this.running = true;
    while (!this.stopThread) {
      try
      {
        Runnable[] runnables = LogUtils.getCachedTracks();
        if (runnables != null) {
          int len = runnables.length;
          for (int i = 0; i < len; i++) {
            this.service.submit(runnables[i]);
          }
        }
        Thread.sleep(LogUtils.getInterval() * 1000L);
      } catch (Exception e) {
        e.printStackTrace();
        log.error("向日志线程池提交任务时出错！", e);
      }
    }
    this.running = false;
  }
}
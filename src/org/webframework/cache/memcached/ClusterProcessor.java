package org.webframework.cache.memcached;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.cache.memcached.client.MemCachedClient;

public class ClusterProcessor extends Thread
{
  private static final Log Logger = LogFactory.getLog(ClusterProcessor.class);
  LinkedBlockingQueue queue;
  MemCachedClientHelper helper;
  boolean isRunning = true;
  private ExecutorService clusterProcessorPool;

  public ClusterProcessor(LinkedBlockingQueue queue, MemCachedClientHelper helper)
  {
    this.queue = queue;
    this.helper = helper;
    this.clusterProcessorPool = Executors.newFixedThreadPool(30);
  }

  public void run() {
    while (this.isRunning)
      process();
  }

  public void stopProcess()
  {
    this.isRunning = false;
    try
    {
      if (this.clusterProcessorPool != null) {
        this.clusterProcessorPool.shutdown();
      }
      this.clusterProcessorPool = null;

      interrupt();
    } catch (Exception ex) {
      Logger.error(ex);
    }
  }

  void process() {
    Object[] commands = (Object[])null;
    try
    {
      commands = (Object[])this.queue.take();

      if ((commands != null) && (this.clusterProcessorPool != null))
        this.clusterProcessorPool.execute(new ClusterUpdateJob(commands));
    } catch (InterruptedException e) {
      Logger.warn("cluster Process stoped!");
    } catch (Exception ex) {
      Logger.error("cluster Process error!", ex);
    }
  }

  public boolean ansyCommandProcess(Object[] commands)
  {
    boolean result = false;

    MemCachedClient innerCache = this.helper.getInnerCacheClient();

    switch (((Integer)commands[0]).intValue()) {
    case 11:
      innerCache.set(commands[1].toString(), commands[2]);
      result = true;
      break;
    case 12:
      innerCache.storeCounter(commands[1].toString(), (Long)commands[2]);
      result = true;
      break;
    case 13:
      innerCache.addOrDecr(commands[1].toString(), 
        ((Long)commands[2]).longValue());
      result = true;
      break;
    case 14:
      innerCache.addOrIncr(commands[1].toString(), 
        ((Long)commands[2]).longValue());
      result = true;
      break;
    case 15:
      innerCache.decr(commands[1].toString(), 
        ((Long)commands[2]).longValue());
      result = true;
      break;
    case 16:
      innerCache.incr(commands[1].toString(), 
        ((Long)commands[2]).longValue());
      result = true;
    }

    return result;
  }

  public void commandProcess(Object[] commands)
  {
    List caches = this.helper.getClusterCache();
    int size = caches.size();

    for (int i = 0; i < size; i++) {
      MemCachedClient cache = (MemCachedClient)caches.get(i);

      if ((((Integer)commands[0]).intValue() == 2) || 
        (((Integer)commands[0]).intValue() == 4)) {
        if (!this.helper.getCacheClient(commands[1].toString()).equals(cache)) {
          continue;
        }
        if (((Integer)commands[0]).intValue() == 2) {
          cache.set(commands[1].toString(), commands[2]); break;
        }
        cache.storeCounter(commands[1].toString(), (Long)commands[2]);
        break;
      }

      if (this.helper.getCacheClient(commands[1].toString()).equals(cache))
        continue;
      try
      {
        switch (((Integer)commands[0]).intValue()) {
        case 1:
        case 11:
          if (commands.length > 3)
            cache.set(commands[1].toString(), commands[2], 
              (Date)commands[3]);
          else
            cache.set(commands[1].toString(), commands[2]);
          break;
        case 9:
          if (commands.length > 3)
            cache.add(commands[1].toString(), commands[2], 
              (Date)commands[3]);
          else
            cache.add(commands[1].toString(), commands[2]);
          break;
        case 10:
          if (commands.length > 3)
            cache.replace(commands[1].toString(), commands[2], 
              (Date)commands[3]);
          else
            cache.replace(commands[1].toString(), commands[2]);
          break;
        case 3:
        case 12:
          cache.storeCounter(commands[1].toString(), 
            (Long)commands[2]);
          break;
        case 5:
        case 13:
          cache.addOrDecr(commands[1].toString(), 
            ((Long)commands[2]).longValue());
          break;
        case 6:
        case 14:
          cache.addOrIncr(commands[1].toString(), 
            ((Long)commands[2]).longValue());
          break;
        case 8:
        case 16:
          cache.incr(commands[1].toString(), 
            ((Long)commands[2]).longValue());
          break;
        case 7:
        case 15:
          cache.decr(commands[1].toString(), 
            ((Long)commands[2]).longValue());
        case 2:
        case 4:
        }
      }
      catch (Exception ex) {
        Logger.error(
          new StringBuffer(this.helper.getCacheName()).append(" cluster process error"), ex);
      }
    }
  }

  class ClusterUpdateJob
    implements Runnable
  {
    Object[] commands;

    public ClusterUpdateJob(Object[] commands)
    {
      this.commands = commands;
    }

    public void run() {
      if (this.commands != null)
      {
        if ((ClusterProcessor.this.ansyCommandProcess(this.commands)) && (!ClusterProcessor.this.helper.hasCluster())) {
          return;
        }
        ClusterProcessor.this.commandProcess(this.commands);
      }
    }
  }
}
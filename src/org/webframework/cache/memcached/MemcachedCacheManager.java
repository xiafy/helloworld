package org.webframework.cache.memcached;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.cache.ICache;
import org.webframework.cache.ICacheManager;
import org.webframework.cache.IMemcachedCache;
import org.webframework.cache.memcached.client.ErrorHandler;
import org.webframework.cache.memcached.client.MemCachedClient;
import org.webframework.cache.memcached.client.SockIOPool;

public class MemcachedCacheManager
  implements ICacheManager
{
  private static final Log Logger = LogFactory.getLog(MemcachedCacheManager.class);
  private static final String MEMCACHED_CONFIG_FILE = "memcached.xml";
  private ConcurrentHashMap cachepool;
  private ConcurrentHashMap socketpool;
  private ConcurrentHashMap clusterpool;
  private ConcurrentHashMap cache2cluster;
  private List memcachedClientconfigs;
  private List memcachedClientSocketPoolConfigs;
  private List memcachedClientClusterConfigs;
  private boolean supportMultiConfig = false;
  private String configFile;
  private int responseStatInterval = 0;
  private URL configFileUrl;

  public void start()
  {
    this.cachepool = new ConcurrentHashMap();
    this.socketpool = new ConcurrentHashMap();
    this.clusterpool = new ConcurrentHashMap();
    this.cache2cluster = new ConcurrentHashMap();

    this.memcachedClientconfigs = new ArrayList();
    this.memcachedClientSocketPoolConfigs = new ArrayList();
    this.memcachedClientClusterConfigs = new ArrayList();

    loadConfig(this.configFile);

    if ((this.memcachedClientconfigs != null) && (this.memcachedClientconfigs.size() > 0) && 
      (this.memcachedClientSocketPoolConfigs != null) && 
      (this.memcachedClientSocketPoolConfigs.size() > 0)) {
      try {
        initMemCacheClientPool();
      } catch (Exception ex) {
        Logger.error("MemcachedManager init error ,please check !");
        throw new RuntimeException(
          "MemcachedManager init error ,please check !", ex);
      }
    }
    else {
      Logger.error("no config info for MemcachedManager,please check !");
      throw new RuntimeException(
        "no config info for MemcachedManager,please check !");
    }
  }

  protected void loadConfig(String configFile)
  {
    try
    {
      if (this.configFileUrl != null) {
        CacheUtil.loadMemcachedConfigFromURL(this.configFileUrl, 
          this.memcachedClientconfigs, 
          this.memcachedClientSocketPoolConfigs, 
          this.memcachedClientClusterConfigs);
        Logger.info(
          new StringBuffer().append("load config from :").append(this.configFileUrl));
      }
      else if (this.supportMultiConfig) {
        Enumeration urls = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if ((configFile != null) && (!configFile.equals("")))
          urls = loader.getResources(configFile);
        else {
          urls = loader.getResources("memcached.xml");
        }
        if ((urls == null) || (!urls.hasMoreElements())) {
          Logger.error("no memcached config find! please put memcached.xml in your classpath");
          throw new RuntimeException("no memcached config find! please put memcached.xml in your classpath");
        }
        do
        {
          URL url = (URL)urls.nextElement();
          CacheUtil.loadMemcachedConfigFromURL(url, 
            this.memcachedClientconfigs, 
            this.memcachedClientSocketPoolConfigs, 
            this.memcachedClientClusterConfigs);

          Logger.info(
            new StringBuffer().append("load config from :").append(url.getFile()));
        }
        while (urls.hasMoreElements());
      }
      else
      {
        URL url = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if ((configFile != null) && (!configFile.equals(""))) {
          if (configFile.startsWith("http"))
            url = new URL(configFile);
          else
            url = loader.getResource(configFile);
        }
        else url = loader.getResource("memcached.xml");

        if (url == null) {
          Logger.error("no memcached config find! please put memcached.xml in your classpath");
          throw new RuntimeException("no memcached config find! please put memcached.xml in your classpath");
        }

        CacheUtil.loadMemcachedConfigFromURL(url, 
          this.memcachedClientconfigs, 
          this.memcachedClientSocketPoolConfigs, 
          this.memcachedClientClusterConfigs);

        Logger.info(
          new StringBuffer().append("load config from :").append(url.getFile()));
      }
    }
    catch (Exception ex) {
      Logger.error("MemcachedManager loadConfig error !");
      throw new RuntimeException("MemcachedManager loadConfig error !", ex);
    }
  }

  protected void initMemCacheClientPool()
  {
    int size = this.memcachedClientSocketPoolConfigs.size();
    for (int m = 0; m < size; m++) {
      MemcachedClientSocketPoolConfig socketPool = 
        (MemcachedClientSocketPoolConfig)this.memcachedClientSocketPoolConfigs.get(m);

      if ((socketPool != null) && (socketPool.getServers() != null) && 
        (!socketPool.getServers().equals(""))) {
        SockIOPool pool = SockIOPool.getNewInstance(socketPool.getName());

        String[] servers = socketPool.getServers().split(",");
        String[] weights = (String[])null;

        if ((socketPool.getWeights() != null) && 
          (!socketPool.getWeights().equals(""))) {
          weights = socketPool.getWeights().split(",");
        }
        pool.setServers(servers);

        if ((weights != null) && (weights.length > 0) && 
          (weights.length == servers.length)) {
          int[] weightsarr = new int[weights.length];

          for (int i = 0; i < weights.length; i++) {
            weightsarr[i] = Integer.parseInt(weights[i]);
          }
          pool.setWeights(weightsarr);
        }

        pool.setInitConn(socketPool.getInitConn());
        pool.setMinConn(socketPool.getMinConn());
        pool.setMaxConn(socketPool.getMaxConn());
        pool.setMaintSleep(socketPool.getMaintSleep());
        pool.setSocketTO(socketPool.getSocketTo());
        pool.setNagle(socketPool.isNagle());
        pool.setFailover(socketPool.isFailover());
        pool.setAliveCheck(socketPool.isAliveCheck());
        pool.setMaxIdle(socketPool.getMaxIdle());
        pool.initialize();

        if (this.socketpool.get(socketPool.getName()) != null) {
          Logger.error(
            new StringBuffer("socketpool define duplicate! socketpool name:").append(socketPool.getName()));
        }
        this.socketpool.put(socketPool.getName(), pool);
        Logger.info(
          new StringBuffer().append(" add socketpool :").append(socketPool.getName()));
      }
      else {
        Logger.error("MemcachedClientSocketPool config error !");
        throw new RuntimeException(
          "MemcachedClientSocketPool config error !");
      }
    }

    size = this.memcachedClientconfigs.size();
    for (int m = 0; m < size; m++) {
      MemcachedClientConfig node = 
        (MemcachedClientConfig)this.memcachedClientconfigs.get(m);

      MemCachedClientHelper helper = new MemCachedClientHelper();
      IMemcachedCache cache = new MemcachedCache(helper, this.responseStatInterval);
      MemCachedClient client = new MemCachedClient(node.getSocketPool());

      client.setCompressEnable(node.isCompressEnable());
      client.setDefaultEncoding(node.getDefaultEncoding());
      try
      {
        if ((node.getErrorHandler() != null) && 
          (!node.getErrorHandler().equals("")))
          client.setErrorHandler(
            (ErrorHandler)Class.forName(
            node.getErrorHandler()).newInstance());
      }
      catch (Exception ex) {
        Logger.error(
          new StringBuffer().append("Not find class name:").append(node.getErrorHandler()).append(
          "please check space char or tab char"));
      }

      helper.setCacheName(node.getName());
      helper.setCacheClient(client);
      helper.setCacheManager(this);
      helper.setMemcachedCache(cache);

      if (this.cachepool.get(node.getName()) != null) {
        Logger.error(
          new StringBuffer("cache define duplicate! cache name :")
          .append(node.getName()));
      }
      this.cachepool.put(node.getName(), cache);
      Logger.info(
        new StringBuffer().append(" add memcachedClient :").append(node.getName()));
    }
    size = this.memcachedClientClusterConfigs.size();
    for (int m = 0; m < size; m++) {
      MemcachedClientClusterConfig node = 
        (MemcachedClientClusterConfig)this.memcachedClientClusterConfigs.get(m);

      String[] clients = node.getMemCachedClients();

      if ((clients != null) && (clients.length > 0)) {
        MemcachedClientCluster cluster = new MemcachedClientCluster();
        cluster.setName(node.getName());
        cluster.setMode(node.getMode());
        cluster.setCaches(new ArrayList());

        int len = clients.length;
        for (int i = 0; i < len; i++) {
          String client = clients[i];
          IMemcachedCache cache = (IMemcachedCache)this.cachepool.get(client);

          if (cache != null) {
            cluster.getCaches().add(cache);
            this.cache2cluster.put(cache, cluster);
          }
        }
        if (this.clusterpool.get(cluster.getName()) != null) {
          Logger.error(
            new StringBuffer("cluster define duplicate! cluster name :").append(cluster.getName()));
        }
        this.clusterpool.put(cluster.getName(), cluster);
      }
    }
  }

  public ICache getCache(String name) {
    return (IMemcachedCache)getCachepool().get(name);
  }

  public void stop()
  {
    try
    {
      Collection values = getCachepool().values();
      for (Iterator it = values.iterator(); it.hasNext(); ) {
        IMemcachedCache node = (IMemcachedCache)it.next();
        if (node != null) {
          node.destroy();
        }
      }
      if ((this.socketpool != null) && (this.socketpool.size() > 0)) {
        Enumeration keys = this.socketpool.keys();

        while (keys.hasMoreElements()) {
          String node = (String)keys.nextElement();
          SockIOPool.removeInstance(node);
        }
        this.socketpool.clear();
      }
    } catch (Exception ex) {
      Logger.error("Cache Manager Stop Error!", ex);
    } finally {
      getCachepool().clear();

      if (this.clusterpool != null) {
        this.clusterpool.clear();
      }
      if (this.cache2cluster != null) {
        this.cache2cluster.clear();
      }
      if (this.memcachedClientconfigs != null) {
        this.memcachedClientconfigs.clear();
      }
      if (this.memcachedClientSocketPoolConfigs != null) {
        this.memcachedClientSocketPoolConfigs.clear();
      }
      if (this.memcachedClientClusterConfigs != null)
        this.memcachedClientClusterConfigs.clear();
    }
  }

  public ConcurrentHashMap getCachepool()
  {
    if (this.cachepool == null) {
      throw new RuntimeException("cachepool is null!");
    }
    return this.cachepool;
  }

  public ConcurrentHashMap getSocketpool() {
    return this.socketpool;
  }

  public void setSocketpool(ConcurrentHashMap socketpool) {
    this.socketpool = socketpool;
  }

  public boolean isSupportMultiConfig() {
    return this.supportMultiConfig;
  }

  public void setSupportMultiConfig(boolean supportMultiConfig) {
    this.supportMultiConfig = supportMultiConfig;
  }

  public ConcurrentHashMap getCache2cluster() {
    return this.cache2cluster;
  }

  public String getConfigFile() {
    return this.configFile;
  }

  public void setConfigFile(String configFile) {
    this.configFile = configFile;
  }

  public void reload(String configFile) {
    if ((configFile != null) && (!configFile.equals(""))) {
      this.configFile = configFile;
    }
    stop();
    start();
  }

  public void clusterCopy(String fromCache, String cluster) {
    IMemcachedCache fcache = (IMemcachedCache)getCachepool()
      .get(fromCache);
    MemcachedClientCluster t_cluster = (MemcachedClientCluster)this.clusterpool
      .get(cluster);

    if ((fcache != null) && (t_cluster != null)) {
      Set keys = fcache.keySet(false);

      List list = t_cluster.getCaches();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        IMemcachedCache cache = (IMemcachedCache)list.get(i);
        if (cache == fcache) {
          continue;
        }
        for (Iterator keyIt = keys.iterator(); keyIt.hasNext(); ) {
          String key = (String)keyIt.next();
          cache.put(key, fcache.get(key));
        }
      }
    }
  }

  public void setResponseStatInterval(int seconds) {
    this.responseStatInterval = seconds;
  }

  public void setConfigFileUrl(URL configFileUrl) {
    this.configFileUrl = configFileUrl;
  }
  public URL getConfigFileUrl() {
    return this.configFileUrl;
  }
}
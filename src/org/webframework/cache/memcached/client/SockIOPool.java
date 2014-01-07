package org.webframework.cache.memcached.client;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentMap;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.CRC32;
import org.apache.log4j.Logger;

public class SockIOPool
{
  private static Logger log = Logger.getLogger(SockIOPool.class.getName());

  private static ConcurrentMap pools = new ConcurrentHashMap();

  private static ThreadLocal MD5 = new ThreadLocal() {
    protected Object initialValue() {
      try {
        return MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
        SockIOPool.log.error("++++ no md5 algorithm found");
      }throw new IllegalStateException("++++ no md5 algorythm found");
    }
  };
  private static final int SOCKET_STATUS_BUSY = 1;
  private static final int SOCKET_STATUS_DEAD = 2;
  private static final int SOCKET_STATUS_ACTIVE = 3;
  public static final int NATIVE_HASH = 0;
  public static final int OLD_COMPAT_HASH = 1;
  public static final int NEW_COMPAT_HASH = 2;
  public static final int CONSISTENT_HASH = 3;
  public static final long MAX_RETRY_DELAY = 600000L;
  public static final Random random = new Random();
  private MaintThread maintThread;
  private boolean initialized = false;
  private int maxCreate = 1;

  private int poolMultiplier = 3;
  private int initConn = 1;
  private int minConn = 1;
  private int maxConn = 10;
  private long maxIdle = 300000L;
  private long maxBusyTime = 30000L;
  private long maintSleep = 30000L;
  private int socketTO = 30000;
  private int socketConnectTO = 3000;

  private static int recBufferSize = 128;

  private boolean aliveCheck = false;

  private boolean failover = true;

  private boolean failback = true;

  private boolean nagle = true;
  private int hashingAlg = 0;

  private final ReentrantLock hostDeadLock = new ReentrantLock();
  private final ReentrantLock initDeadLock = new ReentrantLock();
  private String[] servers;
  private int[] weights;
  private int totalWeight = 0;
  private List buckets;
  private TreeMap consistentBuckets;
  private ConcurrentMap hostDead;
  private ConcurrentMap hostDeadDur;
  private ConcurrentMap socketPool;
  private Map fastPool;
  private static final byte[] B_VERSION = "version\r\n".getBytes();

  public static SockIOPool getInstance(String poolName)
  {
    if (!pools.containsKey(poolName)) {
      SockIOPool pool = new SockIOPool();
      pools.putIfAbsent(poolName, pool);
    }

    return (SockIOPool)pools.get(poolName);
  }

  public static String getPoolUsage(String poolName) {
    StringBuffer result = new StringBuffer();

    if (pools.containsKey(poolName)) {
      SockIOPool sockIOPool = (SockIOPool)pools.get(poolName);

      int total = 0;
      int busy = 0;
      int dead = 0;

      Iterator socketIter = sockIOPool.socketPool.values().iterator();

      while (socketIter.hasNext()) {
        ConcurrentMap status = (ConcurrentMap)socketIter.next();

        total += status.size();

        Iterator iter = status.values().iterator();

        while (iter.hasNext()) {
          int value = ((Integer)iter.next()).intValue();

          if (value == 1) {
            busy++;
          }
          if (value == 2) {
            dead++;
          }
        }
      }
      result.append("SockIOPool ").append(poolName).append(" : ").append(
        " total socket: ").append(total).append(" , busy socket: ")
        .append(busy).append(" , dead socket: ").append(dead);
    }

    return result.toString();
  }

  public static SockIOPool getNewInstance(String poolName)
  {
    if (!pools.containsKey(poolName)) {
      SockIOPool pool = new SockIOPool();
      pools.putIfAbsent(poolName, pool);
    } else {
      SockIOPool newpool = new SockIOPool();
      SockIOPool pool = (SockIOPool)pools.get(poolName);
      pools.put(poolName, newpool);
      try
      {
        pool.shutDown();
      } catch (Exception ex) {
        log.error("shutdown old pool error!", ex);
      }

    }

    return (SockIOPool)pools.get(poolName);
  }

  public static void removeInstance(String poolName) {
    if (pools.containsKey(poolName)) {
      SockIOPool pool = (SockIOPool)pools.get(poolName);
      try
      {
        pool.shutDown();
      } catch (Exception ex) {
        log.error("shutdown old pool error!", ex);
      }

      pools.remove(poolName);
    }
  }

  public static SockIOPool getInstance()
  {
    return getInstance("default");
  }

  public void initialize()
  {
    if ((this.initialized) && ((this.buckets != null) || (this.consistentBuckets != null)) && 
      (this.socketPool != null)) {
      log.error("++++ trying to initialize an already initialized pool");
      return;
    }

    this.initDeadLock.lock();
    try
    {
      if ((this.initialized) && ((this.buckets != null) || (this.consistentBuckets != null)) && 
        (this.socketPool != null)) {
        log.error("++++ trying to initialize an already initialized pool");
        return;
      }
      this.socketPool = new ConcurrentHashMap(this.servers.length * this.initConn);

      this.fastPool = new HashMap();

      this.hostDeadDur = new ConcurrentHashMap();
      this.hostDead = new ConcurrentHashMap();
      this.maxCreate = (this.poolMultiplier > this.minConn ? this.minConn : this.minConn / 
        this.poolMultiplier);

      if (log.isDebugEnabled()) {
        log.debug("++++ initializing pool with following settings:");
        log.debug("++++ initial size: " + this.initConn);
        log.debug("++++ min spare   : " + this.minConn);
        log.debug("++++ max spare   : " + this.maxConn);
      }

      if ((this.servers == null) || (this.servers.length <= 0)) {
        log.error("++++ trying to initialize with no servers");
        throw new IllegalStateException(
          "++++ trying to initialize with no servers");
      }

      if (this.hashingAlg == 3)
        populateConsistentBuckets();
      else {
        populateBuckets();
      }

      this.initialized = true;

      if (this.maintSleep > 0L)
        startMaintThread();
    }
    finally {
      this.initDeadLock.unlock(); } this.initDeadLock.unlock();
  }

  private void populateBuckets()
  {
    if (log.isDebugEnabled()) {
      log.debug("++++ initializing internal hashing structure for consistent hashing");
    }

    this.buckets = new ArrayList();

    for (int i = 0; i < this.servers.length; i++) {
      if ((this.weights != null) && (this.weights.length > i)) {
        for (int k = 0; k < this.weights[i]; k++) {
          this.buckets.add(this.servers[i]);
          if (log.isDebugEnabled())
            log.debug("++++ added " + this.servers[i] + " to server bucket");
        }
      } else {
        this.buckets.add(this.servers[i]);
        if (log.isDebugEnabled()) {
          log.debug("++++ added " + this.servers[i] + " to server bucket");
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("+++ creating initial connections (" + this.initConn + 
          ") for host: " + this.servers[i]);
      }
      for (int j = 0; j < this.initConn; j++) {
        SockIO socket = createSocket(this.servers[i]);
        if (socket == null) {
          log.error("++++ failed to create connection to: " + 
            this.servers[i] + " -- only " + j + " created.");
          break;
        }

        addSocketToPool(this.socketPool, this.servers[i], socket, 
          3, 3, true);

        if (log.isDebugEnabled())
          log.debug("++++ created and added socket: " + 
            socket.toString() + " for host " + this.servers[i]);
      }
    }
  }

  private void populateConsistentBuckets() {
    if (log.isDebugEnabled()) {
      log.debug("++++ initializing internal hashing structure for consistent hashing");
    }

    this.consistentBuckets = new TreeMap();

    MessageDigest md5 = (MessageDigest)MD5.get();
    if ((this.totalWeight <= 0) && (this.weights != null)) {
      for (int i = 0; i < this.weights.length; i++)
        this.totalWeight += (this.weights[i] == 0 ? 1 : this.weights[i]);
    }
    else if (this.weights == null) {
      this.totalWeight = this.servers.length;
    }

    for (int i = 0; i < this.servers.length; i++) {
      int thisWeight = 1;
      if ((this.weights != null) && (this.weights[i] != 0)) {
        thisWeight = this.weights[i];
      }
      double factor = Math.floor(40 * this.servers.length * thisWeight / 
        this.totalWeight);

      for (long j = 0L; j < factor; j += 1L) {
        byte[] d = md5.digest((this.servers[i] + "-" + j).getBytes());
        for (int h = 0; h < 4; h++) {
          Long k = new Long((d[(3 + h * 4)] & 0xFF) << 24 | 
            (d[(2 + h * 4)] & 0xFF) << 16 | 
            (d[(1 + h * 4)] & 0xFF) << 8 | 
            d[(0 + h * 4)] & 0xFF);

          this.consistentBuckets.put(k, this.servers[i]);
          if (log.isDebugEnabled()) {
            log.debug("++++ added " + this.servers[i] + 
              " to server bucket");
          }
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("+++ creating initial connections (" + this.initConn + 
          ") for host: " + this.servers[i]);
      }
      for (int j = 0; j < this.initConn; j++) {
        SockIO socket = createSocket(this.servers[i]);
        if (socket == null) {
          log.error("++++ failed to create connection to: " + 
            this.servers[i] + " -- only " + j + " created.");
          break;
        }

        addSocketToPool(this.socketPool, this.servers[i], socket, 
          3, 3, true);

        if (log.isDebugEnabled())
          log.debug("++++ created and added socket: " + 
            socket.toString() + " for host " + this.servers[i]);
      }
    }
  }

  protected SockIO createSocket(String host)
  {
    SockIO socket = null;

    this.hostDeadLock.lock();

    label116: 
    try { if ((this.failover) && (this.failback) && (this.hostDead.containsKey(host)) && 
        (this.hostDeadDur.containsKey(host)))
      {
        Date store = (Date)this.hostDead.get(host);
        long expire = ((Long)this.hostDeadDur.get(host)).longValue();

        if (store.getTime() + expire <= System.currentTimeMillis()) break label116; return null;
      }
    } finally {
      this.hostDeadLock.unlock();
    }
    try
    {
      socket = new SockIO(this, host, this.socketTO, 
        this.socketConnectTO, this.nagle);

      if (!socket.isConnected()) {
        log.error("++++ failed to get SockIO obj for: " + host + 
          " -- new socket is not connected");
        addSocketToPool(this.socketPool, host, socket, 2, 
          2, true);
      }
    }
    catch (Exception ex) {
      log.error("++++ failed to get SockIO obj for: " + host);
      log.error(ex.getMessage(), ex);
      socket = null;
    }

    this.hostDeadLock.lock();
    try {
      if (socket == null) {
        Date now = new Date();
        this.hostDead.put(host, now);

        long expire = this.hostDeadDur.containsKey(host) ? 
          ((Long)this.hostDeadDur.get(host)).longValue() * 2L : 
          1000L;

        if (expire > 600000L) {
          expire = 600000L;
        }
        this.hostDeadDur.put(host, new Long(expire));
        if (log.isDebugEnabled()) {
          log.debug("++++ ignoring dead host: " + host + " for " + 
            expire + " ms");
        }

        clearHostFromPool(host);
      } else {
        if (log.isDebugEnabled())
          log.debug("++++ created socket (" + socket.toString() + 
            ") for host: " + host);
        if ((this.hostDead.containsKey(host)) || (this.hostDeadDur.containsKey(host))) {
          this.hostDead.remove(host);
          this.hostDeadDur.remove(host);
        }
      }
    } finally {
      this.hostDeadLock.unlock();
    }

    return socket;
  }

  public String getHost(String key)
  {
    return getHost(key, null);
  }

  public String getHost(String key, Integer hashcode)
  {
    SockIO socket = getSock(key, hashcode);
    String host = socket.getHost();
    socket.close();
    return host;
  }

  public SockIO getSock(String key)
  {
    return getSock(key, null);
  }

  public SockIO getSock(String key, Integer hashCode)
  {
    if (log.isDebugEnabled()) {
      log.debug("cache socket pick " + key + " " + hashCode);
    }
    if (!this.initialized) {
      log.error("attempting to get SockIO from uninitialized pool!");
      return null;
    }

    if (((this.hashingAlg == 3) && (this.consistentBuckets.size() == 0)) || (
      (this.buckets != null) && (this.buckets.size() == 0))) {
      return null;
    }

    if (((this.hashingAlg == 3) && (this.consistentBuckets.size() == 0)) || (
      (this.buckets != null) && (this.buckets.size() == 1)))
    {
      SockIO sock = this.hashingAlg == 3 ? 
        getConnection((String)this.consistentBuckets.get(this.consistentBuckets.firstKey())) : 
        getConnection((String)this.buckets.get(0));

      if ((sock != null) && (sock.isConnected())) {
        if ((this.aliveCheck) && 
          (!sock.isAlive())) {
          sock.close();
          try {
            if (this.socketPool.get(sock.getHost()) != null) {
              ((Map)this.socketPool.get(sock.getHost())).remove(sock);
            }
            sock.trueClose();
          } catch (IOException ioe) {
            log.error("failed to close dead socket");
          }

          sock = null;
        }

      }
      else if (sock != null) {
        addSocketToPool(this.socketPool, sock.host, sock, 
          2, 2, true);
      }

      return sock;
    }

    Set tryServers = new HashSet((Collection)Arrays.asList(this.servers));

    long bucket = getBucket(key, hashCode);
    String server = this.hashingAlg == 3 ? 
      (String)this.consistentBuckets.get(new Long(bucket)) : 
      (String)this.buckets.get((int)bucket);

    while (!tryServers.isEmpty())
    {
      SockIO sock = getConnection(server);

      if (log.isDebugEnabled()) {
        log.debug("cache choose " + server + " for " + key);
      }
      if ((sock != null) && (sock.isConnected())) {
        if (this.aliveCheck) {
          if (sock.isAlive()) {
            return sock;
          }
          sock.close();
          try
          {
            if (this.socketPool.get(sock.getHost()) != null)
            {
              ((Map)this.socketPool.get(sock.getHost())).remove(sock);
            }
            sock.trueClose();
          } catch (IOException ioe) {
            log.error("failed to close dead socket");
          }
          sock = null;
        }
        else {
          return sock;
        }
      }
      else if (sock != null) {
        addSocketToPool(this.socketPool, sock.host, sock, 
          2, 2, true);
      }

      if (!this.failover) {
        return null;
      }

      tryServers.remove(server);

      if (tryServers.isEmpty())
      {
        break;
      }

      int rehashTries = 0;
      while (!tryServers.contains(server))
      {
        String newKey = rehashTries + 
          key;

        if (log.isDebugEnabled()) {
          log.debug("rehashing with: " + newKey);
        }
        bucket = getBucket(newKey, null);
        server = this.hashingAlg == 3 ? 
          (String)this.consistentBuckets.get(new Long(bucket)) : 
          (String)this.buckets.get((int)bucket);

        rehashTries++;
      }
    }

    return null;
  }

  public SockIO getConnection(String host)
  {
    if (!this.initialized) {
      log.error("attempting to get SockIO from uninitialized pool!");
      return null;
    }

    if (host == null) {
      return null;
    }

    if ((this.socketPool != null) && (!this.socketPool.isEmpty()))
    {
      Map aSockets = (Map)this.socketPool.get(host);

      SockIO socket = (SockIO)this.fastPool.get(host);
      if ((socket != null) && 
        (isFreeSocket(socket, aSockets))) {
        return socket;
      }

      if ((aSockets != null) && (!aSockets.isEmpty())) {
        int start = random.nextInt() % aSockets.size();

        if (start < 0) {
          start *= -1;
        }
        int count = 0;

        for (Iterator i = aSockets.keySet().iterator(); i.hasNext(); ) {
          if (count < start) {
            i.next();
            count++;
          }
          else
          {
            socket = (SockIO)i.next();

            if (isFreeSocket(socket, aSockets))
              return socket;
          }
        }
        for (Iterator i = aSockets.keySet().iterator(); i.hasNext(); ) {
          if (count <= 0) break;
          socket = (SockIO)i.next();
          if (isFreeSocket(socket, aSockets)) {
            return socket;
          }
          count--;
        }

      }

    }

    SockIO socket = createSocket(host);
    if (socket != null) {
      addSocketToPool(this.socketPool, host, socket, 1, 
        1, true);
    }

    return socket;
  }

  private boolean isFreeSocket(SockIO socket, Map socketMap) {
    if (socket.isConnected()) {
      if (((Integer)socketMap.get(socket)).intValue() == 3)
      {
        if (!addSocketToPool(this.socketPool, socket.getHost(), socket, 
          3, 1, false)) {
          return false;
        }
        if (log.isDebugEnabled()) {
          log.debug("++++ moving socket for host (" + 
            socket.getHost() + ") to busy pool ... socket: " + 
            socket);
        }

        return true;
      }

    }
    else
    {
      addSocketToPool(this.socketPool, socket.getHost(), socket, 
        2, 2, true);
    }

    return false;
  }

  protected boolean addSocketToPool(ConcurrentMap pool, String host, SockIO socket, int oldValue, int newValue, boolean needReplace)
  {
    boolean result = false;

    if (!pool.containsKey(host)) {
      ConcurrentMap sockets = new ConcurrentHashMap();
      pool.putIfAbsent(host, sockets);
    }

    ConcurrentMap sockets = (ConcurrentMap)pool.get(host);

    if (sockets != null) {
      if (needReplace) {
        sockets.put(socket, new Integer(newValue));
        result = true;
      } else {
        return sockets.replace(socket, new Integer(oldValue), 
          new Integer(newValue));
      }
    }

    return result;
  }

  protected void updateStatusPool(String host, SockIO socket, int newStatus)
  {
    if (this.socketPool.containsKey(host))
      ((ConcurrentMap)this.socketPool.get(host)).replace(socket, new Integer(newStatus));
  }

  protected void clearHostFromPool(String host)
  {
    Map sockets = (Map)this.socketPool.remove(host);

    if ((sockets != null) && 
      (sockets.size() > 0)) {
      Iterator it = sockets.keySet().iterator();

      while (it.hasNext()) {
        SockIO socket = (SockIO)it.next();
        sockets.remove(socket);
        try
        {
          socket.trueClose();
        } catch (IOException ioe) {
          log.error("++++ failed to close socket: " + 
            ioe.getMessage());
        }
        socket = null;
      }
    }
  }

  private void checkIn(SockIO socket, boolean addToAvail)
  {
    String host = socket.getHost();
    if (log.isDebugEnabled()) {
      log.debug("++++ calling check-in on socket: " + socket.toString() + 
        " for host: " + host);
    }

    if (log.isDebugEnabled()) {
      log.debug("++++ removing socket (" + socket.toString() + 
        ") from busy pool for host: " + host);
    }
    if ((this.socketPool.containsKey(host)) && 
      (((Map)this.socketPool.get(host)).containsKey(socket)) && 
      (((Integer)((Map)this.socketPool.get(host)).get(socket))
      .intValue() == 1)) {
      addSocketToPool(this.socketPool, host, socket, 3, 
        3, true);

      this.fastPool.put(host, socket);
    }

    if ((socket.isConnected()) && (addToAvail))
    {
      if (log.isDebugEnabled())
        log.debug("++++ returning socket (" + socket.toString() + 
          " to avail pool for host: " + host);
    }
    else addSocketToPool(this.socketPool, host, socket, 2, 
        2, true);
  }

  private void checkIn(SockIO socket)
  {
    checkIn(socket, true);
  }

  protected void closeSocketPool()
  {
    for (Iterator i = this.socketPool.keySet().iterator(); i.hasNext(); ) {
      String host = (String)i.next();
      Map sockets = (Map)this.socketPool.get(host);

      for (Iterator j = sockets.keySet().iterator(); j.hasNext(); ) {
        SockIO socket = (SockIO)j.next();
        sockets.remove(socket);
        try {
          socket.trueClose(false);
        } catch (IOException ioe) {
          log.error("++++ failed to trueClose socket: " + 
            socket.toString() + " for host: " + host);
        }

        socket = null;
      }
    }
  }

  public void shutDown()
  {
    if (log.isDebugEnabled()) {
      log.debug("++++ SockIOPool shutting down...");
    }
    if ((this.maintThread != null) && (this.maintThread.isRunning()))
    {
      stopMaintThread();

      while (this.maintThread.isRunning()) {
        if (log.isDebugEnabled())
          log.debug("++++ waiting for main thread to finish run +++");
        try {
          Thread.sleep(500L);
        }
        catch (Exception localException) {
        }
      }
    }
    if (log.isDebugEnabled())
      log.debug("++++ closing all internal pools.");
    closeSocketPool();

    this.socketPool.clear();
    this.fastPool.clear();
    this.socketPool = null;
    this.fastPool = null;
    this.buckets = null;
    this.consistentBuckets = null;
    this.hostDeadDur = null;
    this.hostDead = null;
    this.maintThread = null;
    this.initialized = false;
    if (log.isDebugEnabled())
      log.debug("++++ SockIOPool finished shutting down.");
  }

  protected void startMaintThread()
  {
    if (this.maintThread != null)
    {
      if (this.maintThread.isRunning())
        log.error("main thread already running");
      else
        this.maintThread.start();
    }
    else {
      this.maintThread = new MaintThread(this);
      this.maintThread.setInterval(this.maintSleep);
      this.maintThread.start();
    }
  }

  protected void stopMaintThread()
  {
    if ((this.maintThread != null) && (this.maintThread.isRunning()))
      this.maintThread.stopThread();
  }

  protected void selfMaint()
  {
    if (log.isDebugEnabled()) {
      log.debug("++++ Starting self maintenance....");
    }

    Map needSockets = new HashMap();

    for (Iterator i = this.socketPool.keySet().iterator(); i.hasNext(); ) {
      String host = (String)i.next();
      ConcurrentMap sockets = (ConcurrentMap)this.socketPool.get(host);

      if (sockets == null) {
        sockets = new ConcurrentHashMap();
        this.socketPool.putIfAbsent(host, sockets);
        sockets = (ConcurrentMap)this.socketPool.get(host);
      }

      if (log.isDebugEnabled()) {
        log.debug("++++ Size of avail pool for host (" + host + ") = " + 
          sockets.size());
      }

      if ((sockets == null) || (sockets.size() >= this.minConn))
        continue;
      int need = this.minConn - sockets.size();
      needSockets.put(host, new Integer(need));
    }

    for (Iterator t1 = needSockets.keySet().iterator(); t1.hasNext(); ) {
      String host = (String)t1.next();
      Integer need = (Integer)needSockets.get(host);

      if (log.isDebugEnabled()) {
        log.debug("++++ Need to create " + need + 
          " new sockets for pool for host: " + host);
      }
      for (int j = 0; j < need.intValue(); j++) {
        SockIO socket = createSocket(host);

        if (socket == null) {
          break;
        }
        addSocketToPool(this.socketPool, host, socket, 3, 
          3, true);
      }
    }

    for (Iterator i = this.socketPool.keySet().iterator(); i.hasNext(); ) {
      String host = (String)i.next();
      ConcurrentMap sockets = (ConcurrentMap)this.socketPool.get(host);

      if (log.isDebugEnabled()) {
        log.debug("++++ Size of avail pool for host (" + host + ") = " + 
          sockets.size());
      }
      int active = 0;

      if (sockets == null) {
        sockets = new ConcurrentHashMap();
        this.socketPool.putIfAbsent(host, sockets);
        sockets = (ConcurrentHashMap)this.socketPool.get(host);
      }

      Iterator iter = sockets.values().iterator();

      while (iter.hasNext()) {
        if (((Integer)iter.next()).intValue() == 3) {
          active++;
        }
      }
      if ((sockets == null) || (active <= this.maxConn))
        continue;
      int diff = active - this.maxConn;
      int needToClose = diff <= this.poolMultiplier ? diff : diff / 
        this.poolMultiplier;

      if (log.isDebugEnabled()) {
        log.debug("++++ need to remove " + needToClose + 
          " spare sockets for pool for host: " + host);
      }
      for (Iterator j = sockets.keySet().iterator(); j.hasNext(); ) {
        if (needToClose <= 0)
        {
          break;
        }
        SockIO socket = (SockIO)j.next();

        if ((((Integer)sockets.get(socket)).intValue() != 3) || 
          (!addSocketToPool(this.socketPool, host, socket, 
          3, 2, false))) continue;
        needToClose--;
      }

    }

    for (Iterator i = this.socketPool.keySet().iterator(); i.hasNext(); ) {
      String host = (String)i.next();
      ConcurrentMap sockets = (ConcurrentMap)this.socketPool.get(host);

      for (Iterator j = sockets.keySet().iterator(); j.hasNext(); )
      {
        SockIO socket = (SockIO)j.next();
        try {
          Integer status = null;
          if ((sockets != null) && (socket != null)) {
            status = (Integer)sockets.get(socket);
          }
          if ((status != null) && (status.intValue() == 2)) {
            if (this.socketPool.containsKey(host)) {
              ((ConcurrentMap)this.socketPool.get(host)).remove(socket);
            }
            socket.trueClose(false);
            socket = null;
          }
        } catch (Exception ex) {
          log.error("++++ failed to close SockIO obj from deadPool");
          log.error(ex.getMessage(), ex);
        }
      }
    }

    if (log.isDebugEnabled())
      log.debug("+++ ending self maintenance.");
  }

  public boolean isInitialized()
  {
    return this.initialized;
  }

  public void setServers(String[] servers)
  {
    this.servers = servers;
  }

  public String[] getServers()
  {
    return this.servers;
  }

  public void setWeights(int[] weights)
  {
    this.weights = weights;
  }

  public int[] getWeights()
  {
    return this.weights;
  }

  public void setInitConn(int initConn)
  {
    this.initConn = initConn;
  }

  public int getInitConn()
  {
    return this.initConn;
  }

  public void setMinConn(int minConn)
  {
    this.minConn = minConn;
  }

  public int getMinConn()
  {
    return this.minConn;
  }

  public void setMaxConn(int maxConn)
  {
    this.maxConn = maxConn;
  }

  public int getMaxConn()
  {
    return this.maxConn;
  }

  public void setMaxIdle(long maxIdle)
  {
    this.maxIdle = maxIdle;
  }

  public long getMaxIdle()
  {
    return this.maxIdle;
  }

  public void setMaxBusyTime(long maxBusyTime)
  {
    this.maxBusyTime = maxBusyTime;
  }

  public long getMaxBusy()
  {
    return this.maxBusyTime;
  }

  public void setMaintSleep(long maintSleep)
  {
    this.maintSleep = maintSleep;
  }

  public long getMaintSleep()
  {
    return this.maintSleep;
  }

  public void setSocketTO(int socketTO)
  {
    this.socketTO = socketTO;
  }

  public int getSocketTO()
  {
    return this.socketTO;
  }

  public void setSocketConnectTO(int socketConnectTO)
  {
    this.socketConnectTO = socketConnectTO;
  }

  public int getSocketConnectTO()
  {
    return this.socketConnectTO;
  }

  public void setFailover(boolean failover)
  {
    this.failover = failover;
  }

  public boolean getFailover()
  {
    return this.failover;
  }

  public void setFailback(boolean failback)
  {
    this.failback = failback;
  }

  public boolean getFailback()
  {
    return this.failback;
  }

  public void setAliveCheck(boolean aliveCheck)
  {
    this.aliveCheck = aliveCheck;
  }

  public boolean getAliveCheck()
  {
    return this.aliveCheck;
  }

  public void setNagle(boolean nagle)
  {
    this.nagle = nagle;
  }

  public boolean getNagle()
  {
    return this.nagle;
  }

  public void setHashingAlg(int alg)
  {
    this.hashingAlg = alg;
  }

  public int getHashingAlg()
  {
    return this.hashingAlg;
  }

  private static long origCompatHashingAlg(String key)
  {
    long hash = 0L;
    char[] cArr = key.toCharArray();

    for (int i = 0; i < cArr.length; i++) {
      hash = hash * 33L + cArr[i];
    }

    return hash;
  }

  private static long newCompatHashingAlg(String key)
  {
    CRC32 checksum = new CRC32();
    checksum.update(key.getBytes());
    long crc = checksum.getValue();
    return crc >> 16 & 0x7FFF;
  }

  private static long md5HashingAlg(String key)
  {
    MessageDigest md5 = (MessageDigest)MD5.get();
    md5.reset();
    md5.update(key.getBytes());
    byte[] bKey = md5.digest();
    long res = (bKey[3] & 0xFF) << 24 | 
      (bKey[2] & 0xFF) << 16 | 
      (bKey[1] & 0xFF) << 8 | bKey[0] & 0xFF;
    return res;
  }

  private long getHash(String key, Integer hashCode)
  {
    if (hashCode != null) {
      if (this.hashingAlg == 3) {
        return hashCode.longValue() & 0xFFFFFFFF;
      }
      return hashCode.longValue();
    }
    switch (this.hashingAlg) {
    case 0:
      return key.hashCode();
    case 1:
      return origCompatHashingAlg(key);
    case 2:
      return newCompatHashingAlg(key);
    case 3:
      return md5HashingAlg(key);
    }

    this.hashingAlg = 0;
    return key.hashCode();
  }

  private long getBucket(String key, Integer hashCode)
  {
    long hc = getHash(key, hashCode);

    if (this.hashingAlg == 3) {
      return findPointFor(new Long(hc)).longValue();
    }
    long bucket = hc % this.buckets.size();
    if (bucket < 0L)
      bucket *= -1L;
    return bucket;
  }

  private Long findPointFor(Long hv)
  {
    SortedMap tmap = this.consistentBuckets.tailMap(hv);

    return (Long)(tmap.isEmpty() ? this.consistentBuckets.firstKey() : 
      tmap.firstKey());
  }
  protected static class MaintThread extends Thread { private static Logger log = Logger.getLogger(MaintThread.class.getName());
    private SockIOPool pool;
    private long interval = 3000L;
    private boolean stopThread = false;
    private boolean running;

    protected MaintThread(SockIOPool pool) { this.pool = pool;
      setDaemon(true);
      setName("MaintThread"); }

    public void setInterval(long interval)
    {
      this.interval = interval;
    }

    public boolean isRunning() {
      return this.running;
    }

    public void stopThread()
    {
      this.stopThread = true;
      interrupt();
    }

    public void run()
    {
      this.running = true;

      while (!this.stopThread) {
        try {
          Thread.sleep(this.interval);

          if (this.pool.isInitialized())
            this.pool.selfMaint();
        }
        catch (Exception e) {
          if ((e instanceof InterruptedException)) {
            log.info("MaintThread stop !"); break;
          }
          log.error("MaintThread error !", e);
          break;
        }
      }

      this.running = false;
    }
  }

  public static class SockIO
  {
    private static Logger log = Logger.getLogger(SockIO.class.getName());
    private SockIOPool pool;
    private String host;
    private Socket sock;
    private DataInputStream in;
    private BufferedOutputStream out;
    private byte[] recBuf;
    private int recBufSize = 1028;
    private int recIndex = 0;

    private long aliveTimeStamp = 0L;

    public SockIO(SockIOPool pool, String host, int port, int timeout, int connectTimeout, boolean noDelay)
      throws IOException, UnknownHostException
    {
      this.pool = pool;

      this.recBuf = new byte[this.recBufSize];

      this.sock = getSocket(host, port, connectTimeout);

      if (timeout >= 0) {
        this.sock.setSoTimeout(timeout);
      }

      this.sock.setTcpNoDelay(noDelay);

      this.in = new DataInputStream(this.sock.getInputStream());
      this.out = new BufferedOutputStream(this.sock.getOutputStream());

      this.host = (host + ":" + port);
    }

    public SockIO(SockIOPool pool, String host, int timeout, int connectTimeout, boolean noDelay)
      throws IOException, UnknownHostException
    {
      this.pool = pool;

      this.recBuf = new byte[this.recBufSize];

      int index = host.indexOf(":");

      if (index <= 0) {
        throw new RuntimeException(
          "host :" + host + " is error,check config file!");
      }

      this.sock = 
        getSocket(host.substring(0, index), Integer.parseInt(
        host.substring(index + 1)), connectTimeout);

      if (timeout >= 0) {
        this.sock.setSoTimeout(timeout);
      }

      this.sock.setTcpNoDelay(noDelay);

      this.in = new DataInputStream(this.sock.getInputStream());
      this.out = new BufferedOutputStream(this.sock.getOutputStream());

      this.host = host;
    }

    protected static Socket getSocket(String host, int port, int timeout)
      throws IOException
    {
      SocketChannel sock = SocketChannel.open();

      sock.connect(new InetSocketAddress(host, port));
      return sock.socket();
    }

    public SocketChannel getChannel()
    {
      return this.sock.getChannel();
    }

    public String getHost()
    {
      return this.host;
    }

    public void trueClose()
      throws IOException
    {
      trueClose(true);
    }

    public void trueClose(boolean addToDeadPool)
      throws IOException
    {
      if (log.isDebugEnabled()) {
        log.debug("++++ Closing socket for real: " + toString());
      }

      this.aliveTimeStamp = 0L;

      this.recBuf = new byte[this.recBufSize];
      this.recIndex = 0;

      boolean err = false;
      StringBuffer errMsg = new StringBuffer();

      if ((this.in == null) || (this.out == null) || (this.sock == null)) {
        err = true;
        errMsg.append("++++ socket or its streams already null in trueClose call");
      }

      if (this.in != null) {
        try {
          this.in.close();
        } catch (IOException ioe) {
          log.error("++++ error closing input stream for socket: " + 
            toString() + " for host: " + getHost());
          log.error(ioe.getMessage(), ioe);
          errMsg.append("++++ error closing input stream for socket: " + 
            toString() + " for host: " + getHost() + "\n");
          errMsg.append(ioe.getMessage());
          err = true;
        }
      }

      if (this.out != null) {
        try {
          this.out.close();
        } catch (IOException ioe) {
          log.error("++++ error closing output stream for socket: " + 
            toString() + " for host: " + getHost());
          log.error(ioe.getMessage(), ioe);
          errMsg.append("++++ error closing output stream for socket: " + 
            toString() + " for host: " + getHost() + "\n");
          errMsg.append(ioe.getMessage());
          err = true;
        }
      }

      if (this.sock != null) {
        try {
          this.sock.close();
        } catch (IOException ioe) {
          log.error("++++ error closing socket: " + toString() + 
            " for host: " + getHost());
          log.error(ioe.getMessage(), ioe);
          errMsg.append("++++ error closing socket: " + toString() + 
            " for host: " + getHost() + "\n");
          errMsg.append(ioe.getMessage());
          err = true;
        }

      }

      if ((addToDeadPool) && (this.sock != null)) {
        this.pool.checkIn(this, false);
      }
      this.in = null;
      this.out = null;
      this.sock = null;

      if (err)
        throw new IOException(errMsg.toString());
    }

    void close()
    {
      if (log.isDebugEnabled()) {
        log.debug("++++ marking socket (" + toString() + 
          ") as closed and available to return to avail pool");
      }
      this.recBuf = new byte[this.recBufSize];
      this.recIndex = 0;

      this.pool.checkIn(this);
    }

    boolean isConnected()
    {
      return (this.sock != null) && (this.sock.isConnected());
    }

    boolean isAlive()
    {
      if (!isConnected())
      {
        this.aliveTimeStamp = 0L;
        return false;
      }

      boolean needcheck = true;

      if (this.aliveTimeStamp > 0L) {
        long interval = System.currentTimeMillis() - this.aliveTimeStamp;

        if (interval < 100L) {
          needcheck = false;
        }
      }
      if (needcheck) {
        try {
          write(SockIOPool.B_VERSION);
          flush();
          readLine();

          this.aliveTimeStamp = System.currentTimeMillis();
        }
        catch (IOException ex) {
          return false;
        }
      }

      return true;
    }

    public byte[] readBytes(int length) throws IOException {
      if ((this.sock == null) || (!this.sock.isConnected())) {
        log.error("++++ attempting to read from closed socket");
        throw new IOException(
          "++++ attempting to read from closed socket");
      }

      byte[] result = (byte[])null;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      if (this.recIndex >= length) {
        bos.write(this.recBuf, 0, length);

        byte[] newBuf = new byte[this.recBufSize];

        if (this.recIndex > length) {
          System.arraycopy(this.recBuf, length, newBuf, 0, this.recIndex - 
            length);
        }
        this.recBuf = newBuf;
        this.recIndex -= length;
      } else {
        int totalread = length;

        if (this.recIndex > 0) {
          totalread -= this.recIndex;
          bos.write(this.recBuf, 0, this.recIndex);

          this.recBuf = new byte[this.recBufSize];
          this.recIndex = 0;
        }

        int readCount = 0;

        while (totalread > 0) {
          if ((readCount = this.in.read(this.recBuf)) > 0) {
            if (totalread > readCount) {
              bos.write(this.recBuf, 0, readCount);
              this.recBuf = new byte[this.recBufSize];
              this.recIndex = 0;
            } else {
              bos.write(this.recBuf, 0, totalread);
              byte[] newBuf = new byte[this.recBufSize];
              System.arraycopy(this.recBuf, totalread, newBuf, 0, 
                readCount - totalread);

              this.recBuf = newBuf;
              this.recIndex = (readCount - totalread);
            }

            totalread -= readCount;
          }
        }

      }

      result = bos.toByteArray();

      if ((result == null) || (
        (result != null) && (result.length <= 0) && (this.recIndex <= 0))) {
        throw new IOException(
          "++++ Stream appears to be dead, so closing it down");
      }

      this.aliveTimeStamp = System.currentTimeMillis();

      return result;
    }

    public String readLine()
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected())) {
        log.error("++++ attempting to read from closed socket");
        throw new IOException(
          "++++ attempting to read from closed socket");
      }

      String result = null;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      int readCount = 0;

      if ((this.recIndex > 0) && (read(bos))) {
        return bos.toString();
      }

      do
      {
        this.recIndex += readCount;

        if (read(bos))
          break;
      }
      while ((readCount = this.in.read(this.recBuf, this.recIndex, this.recBuf.length - 
        this.recIndex)) > 0);

      result = bos.toString();

      if ((result == null) || (
        (result != null) && (result.length() <= 0) && (this.recIndex <= 0))) {
        throw new IOException(
          "++++ Stream appears to be dead, so closing it down");
      }

      this.aliveTimeStamp = System.currentTimeMillis();

      return result;
    }

    private boolean read(ByteArrayOutputStream bos)
    {
      boolean result = false;
      int index = -1;

      for (int i = 0; i < this.recIndex - 1; i++) {
        if ((this.recBuf[i] == 13) && (this.recBuf[(i + 1)] == 10)) {
          index = i;
          break;
        }
      }

      if (index >= 0)
      {
        bos.write(this.recBuf, 0, index);

        byte[] newBuf = new byte[this.recBufSize];

        if (this.recIndex > index + 2) {
          System.arraycopy(this.recBuf, index + 2, newBuf, 0, this.recIndex - 
            index - 2);
        }
        this.recBuf = newBuf;
        this.recIndex = (this.recIndex - index - 2);

        result = true;
      }
      else if (this.recBuf[(this.recIndex - 1)] == 13)
      {
        bos.write(this.recBuf, 0, this.recIndex - 1);
        this.recBuf = new byte[this.recBufSize];
        this.recBuf[0] = 13;
        this.recIndex = 1;
      }
      else {
        bos.write(this.recBuf, 0, this.recIndex);
        this.recBuf = new byte[this.recBufSize];
        this.recIndex = 0;
      }

      return result;
    }

    void flush()
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected())) {
        log.error("++++ attempting to write to closed socket");
        throw new IOException(
          "++++ attempting to write to closed socket");
      }
      this.out.flush();
    }

    void write(byte[] b)
      throws IOException
    {
      if ((this.sock == null) || (!this.sock.isConnected())) {
        log.error("++++ attempting to write to closed socket");
        throw new IOException(
          "++++ attempting to write to closed socket");
      }
      this.out.write(b);
    }

    public int hashCode()
    {
      return this.sock == null ? 0 : this.sock.hashCode();
    }

    public String toString()
    {
      return this.sock == null ? "" : this.sock.toString();
    }

    protected void finalize()
      throws Throwable
    {
      try
      {
        if (this.sock != null) {
          log.error("++++ closing potentially leaked socket in finalize");
          this.sock.close();
          this.sock = null;
        }
      } catch (Throwable t) {
        log.error(t.getMessage(), t);
      }
      finally {
        super.finalize();
      }
    }
  }
}
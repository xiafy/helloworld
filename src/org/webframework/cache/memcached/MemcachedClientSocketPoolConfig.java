package org.webframework.cache.memcached;

public class MemcachedClientSocketPoolConfig
{
  private String name;
  private boolean failover = true;
  private int initConn = 10;
  private int minConn = 5;
  private int maxConn = 250;

  private int maintSleep = 3000;
  private boolean nagle = false;

  private int socketTo = 3000;

  private boolean aliveCheck = true;

  private int maxIdle = 3000;
  private String servers;
  private String weights;

  public String getName()
  {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isFailover() {
    return this.failover;
  }

  public void setFailover(boolean failover) {
    this.failover = failover;
  }

  public int getInitConn() {
    return this.initConn;
  }

  public void setInitConn(int initConn) {
    this.initConn = initConn;
  }

  public int getMinConn() {
    return this.minConn;
  }

  public void setMinConn(int minConn) {
    this.minConn = minConn;
  }

  public int getMaxConn() {
    return this.maxConn;
  }

  public void setMaxConn(int maxConn) {
    this.maxConn = maxConn;
  }

  public int getMaintSleep() {
    return this.maintSleep;
  }

  public void setMaintSleep(int maintSleep) {
    this.maintSleep = maintSleep;
  }

  public boolean isNagle() {
    return this.nagle;
  }

  public void setNagle(boolean nagle) {
    this.nagle = nagle;
  }

  public int getSocketTo() {
    return this.socketTo;
  }

  public void setSocketTo(int socketTo) {
    this.socketTo = socketTo;
  }

  public boolean isAliveCheck() {
    return this.aliveCheck;
  }

  public void setAliveCheck(boolean aliveCheck) {
    this.aliveCheck = aliveCheck;
  }

  public String getServers() {
    return this.servers;
  }

  public void setServers(String servers) {
    this.servers = servers;
  }

  public String getWeights() {
    return this.weights;
  }

  public void setWeights(String weights) {
    this.weights = weights;
  }

  public int getMaxIdle() {
    return this.maxIdle;
  }

  public void setMaxIdle(int maxIdle) {
    this.maxIdle = maxIdle;
  }
}
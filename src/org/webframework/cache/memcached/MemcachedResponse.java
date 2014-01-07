package org.webframework.cache.memcached;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MemcachedResponse
  implements Serializable
{
  private Date startTime;
  private String cacheName;
  private List responses;
  private Date endTime;

  public MemcachedResponse()
  {
    this.responses = new ArrayList();
    ini();
  }

  public void ini() {
    Calendar calendar = Calendar.getInstance();
    this.startTime = calendar.getTime();

    calendar.add(5, 1);
    this.endTime = calendar.getTime();

    this.responses.clear();
  }

  public Date getStartTime() {
    return this.startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public String getCacheName() {
    return this.cacheName;
  }

  public void setCacheName(String cacheName) {
    this.cacheName = cacheName;
  }

  public List getResponses() {
    return this.responses;
  }

  public void setResponses(List responses) {
    this.responses = responses;
  }

  public Date getEndTime() {
    return this.endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String toString() {
    StringBuffer content = new StringBuffer();
    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    content.append("cacheName:").append(this.cacheName);
    content.append(",startTime:").append(formater.format(this.startTime));
    content.append(",endTime:").append(formater.format(this.endTime));
    content.append(",responseRecords:");

    int size = this.responses.size();
    for (int i = 0; i < size; i++) {
      content.append(String.valueOf((Long)this.responses.get(i))).append(",");
    }
    return content.toString();
  }
}
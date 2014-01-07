package org.webframework.tag.grid.exp;

import java.util.Map;

public class Field
{
  private String label;
  private String name;
  private String property;
  private String type;
  private String content;
  private String value;
  private Map map;
  private long seq;
  private String replace;

  public String getLabel()
  {
    return this.label;
  }
  public void setLabel(String label) {
    this.label = label;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getProperty() {
    return this.property;
  }
  public void setProperty(String property) {
    this.property = property;
  }
  public String getType() {
    return this.type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getContent() {
    return this.content;
  }
  public void setContent(String content) {
    this.content = content;
  }
  public long getSeq() {
    return this.seq;
  }
  public void setSeq(long seq) {
    this.seq = seq;
  }
  public Map getMap() {
    return this.map;
  }
  public void setMap(Map map) {
    this.map = map;
  }
  public String getValue() {
    return this.value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public String getReplace() {
    return this.replace;
  }
  public void setReplace(String replace) {
    this.replace = replace;
  }
}
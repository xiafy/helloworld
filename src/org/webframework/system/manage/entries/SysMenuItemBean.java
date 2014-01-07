package org.webframework.system.manage.entries;

import java.io.Serializable;

public class SysMenuItemBean
  implements Serializable
{
  private String cdDm;
  private String cdxDm;
  private String sjCdxDm;
  private String cdxMc;
  private String uri;
  private String dkwz;
  private int zbX;
  private int zbY;
  private int cdxK;
  private int cdxG;
  private int xh;

  public String getCdDm()
  {
    return this.cdDm;
  }
  public void setCdDm(String cdDm) {
    this.cdDm = cdDm;
  }
  public String getCdxDm() {
    return this.cdxDm;
  }
  public void setCdxDm(String cdxDm) {
    this.cdxDm = cdxDm;
  }
  public String getSjCdxDm() {
    return this.sjCdxDm;
  }
  public void setSjCdxDm(String sjCdxDm) {
    this.sjCdxDm = sjCdxDm;
  }
  public String getCdxMc() {
    return this.cdxMc;
  }
  public void setCdxMc(String cdxMc) {
    this.cdxMc = cdxMc;
  }
  public String getUri() {
    return this.uri;
  }
  public void setUri(String uri) {
    this.uri = uri;
  }
  public String getDkwz() {
    return this.dkwz;
  }
  public void setDkwz(String dkwz) {
    this.dkwz = dkwz;
  }
  public int getZbX() {
    return this.zbX;
  }
  public void setZbX(int zbX) {
    this.zbX = zbX;
  }
  public int getZbY() {
    return this.zbY;
  }
  public void setZbY(int zbY) {
    this.zbY = zbY;
  }
  public int getCdxK() {
    return this.cdxK;
  }
  public void setCdxK(int cdxK) {
    this.cdxK = cdxK;
  }
  public int getCdxG() {
    return this.cdxG;
  }
  public void setCdxG(int cdxG) {
    this.cdxG = cdxG;
  }
  public int getXh() {
    return this.xh;
  }
  public void setXh(int xh) {
    this.xh = xh;
  }
}
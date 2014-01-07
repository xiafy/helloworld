package org.webframework;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Ps
{
  private List params = new ArrayList();
  private List paramTypes = new ArrayList();

  public Ps()
  {
  }

  public Ps(Object[] params) {
    int len = params.length;
    for (int i = 0; i < len; i++)
      this.params.add(params[i]);
  }

  protected void setParam(int index, Object value)
  {
    this.params.add(index - 1, value);
  }

  protected void addParam(Object value) {
    this.params.add(value);
  }

  protected void setType(int index, int type) {
    this.paramTypes.add(index - 1, new Integer(type));
  }

  protected void addType(int type) {
    this.paramTypes.add(new Integer(type));
  }

  public Object[] getParams() {
    if (this.params.size() == 0) return null;
    return this.params.toArray(new Object[this.params.size()]);
  }

  public int[] getParamTypes() {
    if (this.paramTypes.size() == 0) return null;

    int[] types = new int[this.paramTypes.size()];
    for (int i = 0; i < this.paramTypes.size(); i++)
      types[i] = ((Integer)this.paramTypes.get(i)).intValue();
    return types;
  }

  public void setParam(int index, Object value, int type)
  {
    setParam(index, value);
    setType(index, type);
  }

  public void addParam(Object value, int type) {
    addParam(value);
    addType(type);
  }

  public void setString(int index, String value) {
    setParam(index, value);
    setType(index, 12);
  }

  public void addString(String value) {
    addParam(value);
    addType(12);
  }

  public void setNullString(int index) {
    setParam(index, null);
    setType(index, 12);
  }

  public void setBoolean(int index, boolean value) {
    setParam(index, new Boolean(value));
    setType(index, 16);
  }

  public void addBoolean(boolean value) {
    addParam(new Boolean(value));
    addType(16);
  }

  public void setNullBoolean(int index) {
    setParam(index, null);
    setType(index, 16);
  }

  public void setBigDecimal(int index, BigDecimal value) {
    setParam(index, value);
    setType(index, 2);
  }

  public void addBigDecimal(BigDecimal value) {
    addParam(value);
    addType(2);
  }

  public void setNullBigDecimal(int index) {
    setParam(index, null);
    setType(index, 2);
  }

  public void setInt(int index, int value) {
    setParam(index, new Integer(value));
    setType(index, 4);
  }

  public void addInt(int value) {
    addParam(new Integer(value));
    addType(4);
  }

  public void setNullInt(int index) {
    setParam(index, null);
    setType(index, 4);
  }

  public void setLong(int index, long value) {
    setParam(index, new Long(value));
    setType(index, -5);
  }

  public void addLong(long value) {
    addParam(new Long(value));
    addType(-5);
  }

  public void setNullLong(int index) {
    setParam(index, null);
    setType(index, -5);
  }

  public void setFloat(int index, float value) {
    setParam(index, new Float(value));
    setType(index, 6);
  }

  public void addFloat(float value) {
    addParam(new Float(value));
    addType(6);
  }

  public void setNullFloat(int index) {
    setParam(index, null);
    setType(index, 6);
  }

  public void setDouble(int index, double value) {
    setParam(index, new Double(value));
    setType(index, 8);
  }

  public void addDouble(double value) {
    addParam(new Double(value));
    addType(8);
  }

  public void setNullDouble(int index) {
    setParam(index, null);
    setType(index, 8);
  }

  public void setBlob(int index, Blob value) {
    setParam(index, value);
    setType(index, 2004);
  }

  public void addBlob(Blob value) {
    addParam(value);
    addType(2004);
  }

  public void setNullBlob(int index) {
    setParam(index, null);
    setType(index, 2004);
  }

  public void setClob(int index, Clob value) {
    setParam(index, value);
    setType(index, 2005);
  }

  public void addClob(Clob value) {
    addParam(value);
    addType(2005);
  }

  public void setNullClob(int index) {
    setParam(index, null);
    setType(index, 2005);
  }

  public void setDate(int index, java.util.Date date) {
    setParam(index, new java.sql.Date(date.getTime()));
    setType(index, 91);
  }

  public void addDate(java.util.Date date) {
    addParam(new java.sql.Date(date.getTime()));
    addType(91);
  }

  public void setNullDate(int index) {
    setParam(index, null);
    setType(index, 91);
  }

  public void setDate(int index, java.sql.Date date) {
    setParam(index, date);
    setType(index, 91);
  }

  public void addDate(java.sql.Date date) {
    addParam(date);
    addType(91);
  }

  public void setTime(int index, Time time) {
    setParam(index, time);
    setType(index, 92);
  }

  public void addTime(Time time) {
    addParam(time);
    addType(92);
  }

  public void setTime(int index, java.util.Date time) {
    setParam(index, new Time(time.getTime()));
    setType(index, 92);
  }

  public void setNullTime(int index) {
    setParam(index, null);
    setType(index, 92);
  }

  public void setTimestamp(int index, Timestamp time) {
    setParam(index, time);
    setType(index, 93);
  }

  public void addTimestamp(Timestamp time) {
    addParam(time);
    addType(93);
  }

  public void setNullTimestamp(int index) {
    setParam(index, null);
    setType(index, 93);
  }

  public void setNull(int index, int columnType) {
    setParam(index, null);
    setType(index, columnType);
  }
}
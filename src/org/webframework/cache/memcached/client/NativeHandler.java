package org.webframework.cache.memcached.client;

import java.util.Date;

public class NativeHandler
{
  public static boolean isHandled(Object value)
  {
    return ((value instanceof Byte)) || ((value instanceof Boolean)) || 
      ((value instanceof Integer)) || ((value instanceof Long)) || 
      ((value instanceof Character)) || ((value instanceof String)) || 
      ((value instanceof StringBuffer)) || ((value instanceof Float)) || 
      ((value instanceof Short)) || ((value instanceof Double)) || 
      ((value instanceof Date)) || 
      ((value instanceof byte[]));
  }

  public static int getMarkerFlag(Object value)
  {
    if ((value instanceof Byte)) {
      return 1;
    }
    if ((value instanceof Boolean)) {
      return 8192;
    }
    if ((value instanceof Integer)) {
      return 4;
    }
    if ((value instanceof Long)) {
      return 16384;
    }
    if ((value instanceof Character)) {
      return 16;
    }
    if ((value instanceof String)) {
      return 32;
    }
    if ((value instanceof StringBuffer)) {
      return 64;
    }
    if ((value instanceof Float)) {
      return 128;
    }
    if ((value instanceof Short)) {
      return 256;
    }
    if ((value instanceof Double)) {
      return 512;
    }
    if ((value instanceof Date)) {
      return 1024;
    }

    if ((value instanceof byte[])) {
      return 4096;
    }
    return -1;
  }

  public static byte[] encode(Object value)
    throws Exception
  {
    if ((value instanceof Byte)) {
      return encode((Byte)value);
    }
    if ((value instanceof Boolean)) {
      return encode((Boolean)value);
    }
    if ((value instanceof Integer)) {
      return encode(((Integer)value).intValue());
    }
    if ((value instanceof Long)) {
      return encode(((Long)value).longValue());
    }
    if ((value instanceof Character)) {
      return encode((Character)value);
    }
    if ((value instanceof String)) {
      return encode((String)value);
    }
    if ((value instanceof StringBuffer)) {
      return encode((StringBuffer)value);
    }
    if ((value instanceof Float)) {
      return encode(((Float)value).floatValue());
    }
    if ((value instanceof Short)) {
      return encode((Short)value);
    }
    if ((value instanceof Double)) {
      return encode(((Double)value).doubleValue());
    }
    if ((value instanceof Date)) {
      return encode((Date)value);
    }

    if ((value instanceof byte[])) {
      return encode((byte[])value);
    }
    return null;
  }

  protected static byte[] encode(Byte value) {
    byte[] b = new byte[1];
    b[0] = value.byteValue();
    return b;
  }

  protected static byte[] encode(Boolean value) {
    byte[] b = new byte[1];

    if (value.booleanValue())
      b[0] = 1;
    else {
      b[0] = 0;
    }
    return b;
  }

  protected static byte[] encode(int value) {
    return getBytes(value);
  }

  protected static byte[] encode(long value) throws Exception {
    return getBytes(value);
  }

  protected static byte[] encode(Date value) {
    return getBytes(value.getTime());
  }

  protected static byte[] encode(Character value) {
    return encode(value.charValue());
  }

  protected static byte[] encode(String value) throws Exception {
    return value.getBytes("UTF-8");
  }

  protected static byte[] encode(StringBuffer value) throws Exception {
    return encode(value.toString());
  }

  protected static byte[] encode(float value) throws Exception {
    return encode(Float.floatToIntBits(value));
  }

  protected static byte[] encode(Short value) throws Exception {
    return encode(value.shortValue());
  }

  protected static byte[] encode(double value) throws Exception {
    return encode(Double.doubleToLongBits(value));
  }

  protected static byte[] encode(byte[] value)
  {
    return value;
  }

  protected static byte[] getBytes(long value) {
    byte[] b = new byte[8];
    b[0] = (byte)(int)(value >> 56 & 0xFF);
    b[1] = (byte)(int)(value >> 48 & 0xFF);
    b[2] = (byte)(int)(value >> 40 & 0xFF);
    b[3] = (byte)(int)(value >> 32 & 0xFF);
    b[4] = (byte)(int)(value >> 24 & 0xFF);
    b[5] = (byte)(int)(value >> 16 & 0xFF);
    b[6] = (byte)(int)(value >> 8 & 0xFF);
    b[7] = (byte)(int)(value >> 0 & 0xFF);
    return b;
  }

  protected static byte[] getBytes(int value) {
    byte[] b = new byte[4];
    b[0] = (byte)(value >> 24 & 0xFF);
    b[1] = (byte)(value >> 16 & 0xFF);
    b[2] = (byte)(value >> 8 & 0xFF);
    b[3] = (byte)(value >> 0 & 0xFF);
    return b;
  }

  public static Object decode(byte[] b, int flag)
    throws Exception
  {
    if (b.length < 1) {
      return null;
    }
    if ((flag & 0x1) == 1) {
      return decodeByte(b);
    }
    if ((flag & 0x2000) == 8192) {
      return decodeBoolean(b);
    }
    if ((flag & 0x4) == 4) {
      return decodeInteger(b);
    }
    if ((flag & 0x4000) == 16384) {
      return decodeLong(b);
    }
    if ((flag & 0x10) == 16) {
      return decodeCharacter(b);
    }
    if ((flag & 0x20) == 32) {
      return decodeString(b);
    }
    if ((flag & 0x40) == 64) {
      return decodeStringBuffer(b);
    }
    if ((flag & 0x80) == 128) {
      return decodeFloat(b);
    }
    if ((flag & 0x100) == 256) {
      return decodeShort(b);
    }
    if ((flag & 0x200) == 512) {
      return decodeDouble(b);
    }
    if ((flag & 0x400) == 1024) {
      return decodeDate(b);
    }

    if ((flag & 0x1000) == 4096) {
      return decodeByteArr(b);
    }
    return null;
  }

  protected static Byte decodeByte(byte[] b)
  {
    return new Byte(b[0]);
  }

  protected static Boolean decodeBoolean(byte[] b) {
    boolean value = b[0] == 1;
    return value ? Boolean.TRUE : Boolean.FALSE;
  }

  protected static Integer decodeInteger(byte[] b) {
    return new Integer(toInt(b));
  }

  protected static Long decodeLong(byte[] b) throws Exception {
    return new Long(toLong(b));
  }

  protected static Character decodeCharacter(byte[] b) {
    return new Character((char)decodeInteger(b).intValue());
  }

  protected static String decodeString(byte[] b) throws Exception {
    return new String(b, "UTF-8");
  }

  protected static StringBuffer decodeStringBuffer(byte[] b) throws Exception {
    return new StringBuffer(decodeString(b));
  }

  protected static Float decodeFloat(byte[] b) throws Exception {
    Integer l = decodeInteger(b);
    return new Float(Float.intBitsToFloat(l.intValue()));
  }

  protected static Short decodeShort(byte[] b) throws Exception {
    return new Short((short)decodeInteger(b).intValue());
  }

  protected static Double decodeDouble(byte[] b) throws Exception {
    Long l = decodeLong(b);
    return new Double(Double.longBitsToDouble(l.longValue()));
  }

  protected static Date decodeDate(byte[] b) {
    return new Date(toLong(b));
  }

  protected static byte[] decodeByteArr(byte[] b)
  {
    return b;
  }

  protected static int toInt(byte[] b)
  {
    return ((b[3] & 0xFF) << 32) + ((b[2] & 0xFF) << 40) + (
      (b[1] & 0xFF) << 48) + ((b[0] & 0xFF) << 56);
  }

  protected static long toLong(byte[] b) {
    return (b[7] & 0xFF) + ((b[6] & 0xFF) << 8) + (
      (b[5] & 0xFF) << 16) + (
      (b[4] & 0xFF) << 24) + (
      (b[3] & 0xFF) << 32) + (
      (b[2] & 0xFF) << 40) + (
      (b[1] & 0xFF) << 48) + ((b[0] & 0xFF) << 56);
  }
}
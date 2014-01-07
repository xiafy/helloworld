package org.webframework.system;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.webframework.DB;
import org.webframework.WMap;
import org.webframework.db.dialect.syntax.SyntaxDialect;

public class SysUtils
{
  public static String getSeqNextValue(String seqName)
    throws Exception
  {
    return DB.getSyntaxDialect().getSeqNextValue(seqName);
  }

  public static String getSeqNextValueByNum(String seqName, int num)
    throws Exception
  {
    String seq = DB.getSyntaxDialect().getSeqNextValue(seqName);
    int len = num - seq.length();
    StringBuffer prefiex = new StringBuffer();
    if (len > 0) {
      for (int i = 0; i < len; i++) {
        prefiex.append("0");
      }
    }
    return seq;
  }

  public static String getTimeWeb(String format)
  {
    SimpleDateFormat df = new SimpleDateFormat(format);
    return df.format(new Date());
  }

  public static String getDateTimeDb()
  {
    return DB.getSyntaxDialect().getDateTime();
  }

  public static String getTimestampDb()
  {
    return DB.getSyntaxDialect().getTimestamp();
  }

  public static boolean equals(String s1, String s2)
  {
    if (s1.length() != s2.length()) {
      return false;
    }
    if (s1.hashCode() != s2.hashCode()) {
      return false;
    }

    return s1.equals(s2);
  }

  public static Boolean getBoolean(String value, boolean defaultValue)
  {
    if ((value == null) || ("".equals(value.trim()))) {
      return !defaultValue ? Boolean.FALSE : Boolean.TRUE;
    }
    value = value.trim();
    if ("true".equalsIgnoreCase(value)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
  public static Boolean getBoolean(String value) {
    return getBoolean(value, false);
  }

  public static Map convertBean2Map(Object obj)
  {
    if (obj == null) {
      return null;
    }
    WMap wmap = new WMap();
    try {
      wmap.putAll(PropertyUtils.describe(obj));
    } catch (Exception e) {
      e.printStackTrace();
    }
    wmap.remove("class");
    return wmap;
  }

  public static Object convertMap2Bean(Map map, Object obj)
  {
    if (obj == null) {
      return null;
    }
    if (map == null)
      return obj;
    try
    {
      PropertyUtils.copyProperties(obj, map);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return obj;
  }

  public static Object convertMap2Bean(Map map, Class c)
  {
    if (c == null) {
      return null;
    }
    Object obj = null;
    try {
      obj = convertMap2Bean(map, c.newInstance());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return obj;
  }
}
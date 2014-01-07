package org.webframework.tag.grid.exp;

import java.util.HashMap;
import java.util.Map;

public class FieldType
{
  public static final String TYPE_SEQ = "seq";
  public static final String TYPE_TEXT = "text";
  public static final String TYPE_LABEL = "label";
  public static final String TYPE_HIDDEN = "hidden";
  public static final String TYPE_SELECT = "select";
  public static final String TYPE_RADIO = "radio";
  public static final String TYPE_CHECKBOX = "checkbox";
  public static final String TYPE_DATE = "date";
  public static final String TYPE_PASSWORD = "password";
  public static final String TYPE_TEXTAREA = "textarea";

  public static Map getSelect(String data)
  {
    Map map = new HashMap();
    if ((data != null) && (!"".equals(data))) {
      String[] entry = data.split("\\,");

      int len = entry.length;
      for (int i = 0; i < len; i++) {
        String[] kv = entry[i].split("\\:", -1);
        map.put(kv[0], kv[1]);
      }
    }
    return map;
  }
}
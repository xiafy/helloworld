package org.webframework.system.manage.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONArray;
import org.webframework.WMap;

public class TreeUtils
{
  public static String getList2JsonAry(List data, Object fieldsAry, HandleNode nodeType)
  {
    List fields = null;
    if (fieldsAry != null) {
      if ((fieldsAry instanceof List)) {
        fields = (List)fieldsAry;
      } else if ((fieldsAry instanceof String[])) {
        fields = new ArrayList();
        String[] fieldsAryString = (String[])fieldsAry;
        int len = fieldsAryString.length;
        for (int i = 0; i < len; i++) {
          fields.add(fieldsAryString[i]);
        }
      }
    }

    List listAry = new ArrayList();
    if (data == null) {
      data = new ArrayList();
    }
    if (!data.isEmpty()) {
      int size = data.size();
      WMap map = null;
      if (fields == null)
      {
        fields = new ArrayList();
        for (int i = 0; i < size; i++) {
          map = (WMap)data.get(i);
          if (nodeType != null) {
            map = nodeType.handle(map);
          }

          if (map != null) {
            for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
              fields.add(it.next());
            }
            break;
          }
        }
      }

      int len = fields.size();
      for (int i = 0; i < size; i++) {
        map = (WMap)data.get(i);
        if (nodeType != null) {
          map = nodeType.handle(map);
        }
        if (map != null) {
          List t = new ArrayList();
          for (int j = 0; j < len; j++) {
            t.add(map.get(fields.get(j)));
          }
          listAry.add(t);
        }
      }
    }
    return JSONArray.toJSONString(listAry);
  }

  public static String getList2JsonAry(List data) {
    return getList2JsonAry(data, null, null);
  }
  public static String getList2JsonAry(List data, List fields) {
    return getList2JsonAry(data, fields, null);
  }
  public static String getList2JsonAry(List data, String[] fields) {
    return getList2JsonAry(data, fields, null);
  }
  public static String getList2JsonAry(List data, HandleNode nodeType) {
    return getList2JsonAry(data, null, nodeType);
  }

  public static String getList2Json(List data, Object fieldsAry, HandleNode nodeType)
  {
    List fields = null;
    if (fieldsAry != null) {
      if ((fieldsAry instanceof List)) {
        fields = (List)fieldsAry;
      } else if ((fieldsAry instanceof String[])) {
        fields = new ArrayList();
        String[] fieldsAryString = (String[])fieldsAry;
        int len = fieldsAryString.length;
        for (int i = 0; i < len; i++) {
          fields.add(fieldsAryString[i]);
        }
      }
    }

    if (data == null) {
      data = new ArrayList();
    }
    if (!data.isEmpty()) {
      WMap map = null;
      int size = data.size();
      List list = new ArrayList();
      if (fields != null) {
        int len = fields.size();
        for (int i = 0; i < size; i++) {
          map = (WMap)data.get(i);
          if (nodeType != null) {
            map = nodeType.handle(map);
          }
          if (map != null) {
            WMap tmap = new WMap();
            for (int j = 0; j < len; j++) {
              tmap.put(fields.get(j), map.get(fields.get(j)));
            }
            list.add(tmap);
          }
        }
        data = list;
      }
      else if (nodeType != null) {
        for (int i = 0; i < size; i++) {
          map = (WMap)data.get(i);
          map = nodeType.handle(map);
          if (map != null) {
            list.add(map);
          }
        }
        data = list;
      }
    }

    return JSONArray.toJSONString(data);
  }

  public static String getList2Json(List data) {
    return getList2Json(data, null, null);
  }
  public static String getList2Json(List data, List fields) {
    return getList2Json(data, fields, null);
  }
  public static String getList2Json(List data, String[] fields) {
    return getList2Json(data, fields, null);
  }
  public static String getList2Json(List data, HandleNode nodeType) {
    return getList2Json(data, null, nodeType);
  }
}
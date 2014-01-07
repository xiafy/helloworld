package org.webframework.tag.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.webframework.WMap;
import org.webframework.mvc.View;
import org.webframework.tag.grid.exp.Field;
import org.webframework.tag.grid.exp.Model;

public class GridParams
{
  private static Log log = LogFactory.getLog(GridParams.class);
  public static final String GRID_PREFIX = "_grid_";
  public static final String GRID_DATA_PREFIX = "_grid_data_";
  public static final String GRID_PARAMS_PREFIX = "_grid_params_";
  public static final String GRID_KEY_EXP_GRIDNAME = "_grid_key_exp_gridname";
  public static final String SPLIT_NAME = "#n#";
  public static final String SPLIT_ROW = "#r#";
  public static final String SPLIT_COL = "#c#";
  private String name;
  private long page;
  private long pageSize;
  private long total;
  private boolean showPage;
  private String expType;
  private Map expModel;
  private Model model;
  private List list = new ArrayList();
  private View view;

  public GridParams()
  {
  }

  public GridParams(String key, View view)
  {
    this.view = view;

    parseParams(view.getString(key));
    parseData();
    parseModel();
    clearData();
  }

  private void parseParams(String data)
  {
    if ((data != null) && (!"".equals(data))) {
      JSONParser p = new JSONParser();
      try {
        PropertyUtils.copyProperties(this, p.parse(data));
      } catch (Exception e) {
        e.printStackTrace();
        log.error("解析Grid参数失败,请联系管理员!", e);
      }
    }
  }

  private void parseData()
  {
    String data = this.view.getString("_grid_data_" + this.name);
    if ((data != null) && (!"".equals(data))) {
      boolean isinit = false;
      WMap wmap = null;

      String[] cols = data.split("#c#", -1);
      int collen = cols.length;
      for (int i = 0; i < collen; i++) {
        String[] rows = cols[i].split("#n#", -1);
        String name = rows[0].split("\\.")[1];
        String[] values = rows[1].split("#r#", -1);
        int rowlen = values.length - 1;
        if (!isinit) {
          for (int j = 0; j < rowlen; j++) {
            this.list.add(new WMap());
          }
          isinit = true;
        }
        for (int j = 0; j < rowlen; j++) {
          wmap = (WMap)this.list.get(j);
          wmap.put(name, values[j]);
        }
      }
    }
  }

  private void parseModel()
  {
    if ((this.expType != null) && (!"".equals(this.expType))) {
      this.view.put("_grid_key_exp_gridname", this.name);
    }

    if (this.expModel != null) {
      this.model = new Model();
      JSONParser p = new JSONParser();
      try {
        PropertyUtils.copyProperties(this.model, this.expModel);
        List t = this.model.getFields();
        List fields = new ArrayList();
        int size = t.size();
        for (int i = 0; i < size; i++) {
          Field field = new Field();
          JSONObject json = (JSONObject)t.get(i);
          PropertyUtils.copyProperties(field, p.parse(json.toJSONString()));
          fields.add(field);
        }
        Collections.sort(fields, new FieldComparator());
        this.model.setFields(fields);
        this.model.init();
      } catch (Exception e) {
        e.printStackTrace();
        log.error("解析Excel数据结构失败,请联系管理员!", e);
      }
    }
  }

  private void clearData()
  {
    if (this.view != null) {
      this.view.remove("_grid_data_" + this.name);
      this.view.remove("_grid_params_" + this.name);

      Iterator it = this.view.keySet().iterator();
      String gn = this.name + ".";
      List removeKeys = new ArrayList();
      while (it.hasNext()) {
        String key = (String)it.next();
        if (key.indexOf(gn) == 0) {
          removeKeys.add(key);
        }
      }
      int len = removeKeys.size();
      for (int i = 0; i < len; i++)
        this.view.remove((String)removeKeys.get(i));
    }
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getPage() {
    return this.page;
  }

  public void setPage(long page) {
    this.page = page;
  }

  public long getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(long pageSize) {
    this.pageSize = pageSize;
  }

  public boolean isShowPage() {
    return this.showPage;
  }

  public void setShowPage(boolean showPage) {
    this.showPage = showPage;
  }

  public String getExpType() {
    return this.expType;
  }

  public void setExpType(String expType) {
    this.expType = expType;
  }

  public Map getExpModel() {
    return this.expModel;
  }

  public void setExpModel(Map expModel) {
    this.expModel = expModel;
  }

  public Model getModel() {
    return this.model;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public List getList() {
    return this.list;
  }

  public void setList(List list) {
    this.list = list;
  }

  public View getView() {
    return this.view;
  }

  public void setView(View view) {
    this.view = view;
  }

  public long getTotal() {
    return this.total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  class FieldComparator
    implements Comparator
  {
    FieldComparator()
    {
    }

    public int compare(Object e1, Object e2)
    {
      return (int)(((Field)e1).getSeq() - ((Field)e2).getSeq());
    }
  }
}
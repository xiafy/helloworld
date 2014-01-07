package org.webframework.tag.grid.exp;

import java.util.ArrayList;
import java.util.List;

public class Model
{
  private boolean showLineNum;
  private String exportFileName;
  private List expFields;
  private List fields;

  public void init()
  {
    if (this.fields != null) {
      this.expFields = new ArrayList();
      int size = this.fields.size();

      for (int i = 0; i < size; i++) {
        Field field = (Field)this.fields.get(i);
        String type = field.getType();

        if ((!"seq".equals(type)) && (!"text".equals(type)) && 
          (!"label".equals(type)) && (!"select".equals(type)) && 
          (!"textarea".equals(type)) && (!"date".equals(type)))
          continue;
        this.expFields.add(field);
        if (type.equals("select"))
          field.setMap(FieldType.getSelect(field.getContent()));
      }
    }
  }

  public boolean isShowLineNum()
  {
    return this.showLineNum;
  }
  public void setShowLineNum(boolean showLineNum) {
    this.showLineNum = showLineNum;
  }
  public String getExportFileName() {
    return this.exportFileName;
  }
  public void setExportFileName(String exportFileName) {
    this.exportFileName = exportFileName;
  }
  public List getFields() {
    return this.fields;
  }
  public void setFields(List fields) {
    this.fields = fields;
  }
  public List getExpFields() {
    return this.expFields;
  }
  public void setExpFields(List expFields) {
    this.expFields = expFields;
  }
}
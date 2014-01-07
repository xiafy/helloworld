package org.webframework.mvc;

import java.util.ArrayList;
import java.util.List;
import org.webframework.WMap;
import org.webframework.tag.grid.Grid;

public class AjaxView extends WMap
{
  private boolean hasError;
  private String errorMsg;

  public AjaxView(View params)
  {
    putAll(params);
  }

  public List getGridData(String gridName)
  {
    Grid grid = getGrid(gridName, false);
    if (grid != null) {
      return grid.getList();
    }
    return new ArrayList();
  }

  public Grid getGrid(String gridName)
  {
    return getGrid(gridName, true);
  }

  public Grid getGrid(String gridName, boolean create) {
    Grid grid = (Grid)get(gridName);
    if ((grid == null) || (!(grid instanceof Grid))) {
      if (create) {
        return new Grid(gridName);
      }
      return null;
    }

    return grid;
  }
  public boolean hasError() {
    return this.hasError;
  }
  public void setHasError(boolean hasError) {
    this.hasError = hasError;
  }
  public String getErrorMsg() {
    return this.errorMsg;
  }
  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
    setHasError(true);
  }
}
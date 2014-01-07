package org.webframework.mvc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.webframework.WMap;
import org.webframework.mvc.wrapper.fileupload.MultipartFile;
import org.webframework.tag.grid.Grid;

public class View extends WMap
{
  private boolean isRedirect = false;
  private String nextJspPath;
  private Map multipartFiles;

  public void setMultipartFiles(Map multipartFiles)
  {
    this.multipartFiles = multipartFiles;
  }

  public Iterator getFileNames() {
    return this.multipartFiles.keySet().iterator();
  }

  public MultipartFile getFile(String name) {
    return (MultipartFile)this.multipartFiles.get(name);
  }

  public Map getFileMap() {
    return this.multipartFiles;
  }

  public boolean isRedirect()
  {
    return this.isRedirect;
  }

  public void setRedirect(boolean isRedirect) {
    this.isRedirect = isRedirect;
  }

  public String getNextJspPath() {
    return this.nextJspPath;
  }

  public void setNextJspPath(String nextJspPath) {
    this.nextJspPath = nextJspPath;
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
}
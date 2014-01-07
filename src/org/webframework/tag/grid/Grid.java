package org.webframework.tag.grid;

import java.util.ArrayList;
import java.util.List;
import org.webframework.tag.grid.exp.Model;

public class Grid
{
  private GridParams params;
  private String name;
  private int page = 1;
  private int pageSize = 15;
  private boolean showPage = true;
  private String expType;
  private Model model;
  private List list = new ArrayList();
  private int total = 0;

  public Grid(String name) {
    this.name = name;
  }

  public Grid(GridParams params) {
    this.params = params;
    this.name = params.getName();
    long page = params.getPage();
    if (page != 0L) {
      this.page = (int)page;
    }
    long pageSize = params.getPageSize();
    if (pageSize != 0L) {
      this.pageSize = (int)pageSize;
    }
    long total = params.getTotal();
    if (total != 0L) {
      this.total = (int)total;
    }
    this.expType = params.getExpType();
    if ((this.expType != null) && (!"".equals(this.expType)))
      this.showPage = false;
    else {
      this.showPage = params.isShowPage();
    }
    this.model = params.getModel();
    this.list = params.getList();
  }

  public void clearData() {
    if (this.list != null)
      this.list.clear();
    else
      this.list = new ArrayList();
  }

  public GridParams getParams()
  {
    return this.params;
  }

  public void setParams(GridParams params) {
    this.params = params;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPage() {
    return this.page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(int pageSize) {
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

  public int getTotal() {
    return this.total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
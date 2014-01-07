package org.webframework.system.manage.entries;

public class PathTable
{
  private String tableName;
  private String id;
  private String idField;
  private String pId;
  private String pIdField;
  private String pathField;
  private String nameField;

  public PathTable()
  {
  }

  public PathTable(String tableName)
  {
    setTableName(tableName);
  }

  public String getTableName() {
    return this.tableName;
  }
  public PathTable setTableName(String tableName) {
    this.tableName = tableName;
    return this;
  }
  public String getId() {
    return this.id;
  }
  public PathTable setId(String id) {
    this.id = id;
    return this;
  }
  public String getIdField() {
    return this.idField == null ? "GNMK_DM" : this.idField;
  }
  public PathTable setIdField(String idField) {
    this.idField = idField;
    return this;
  }
  public String getPId() {
    return this.pId;
  }
  public PathTable setPId(String pId) {
    this.pId = pId;
    return this;
  }

  public String getPIdField() {
    return this.pIdField == null ? "SJ_GNMK_DM" : this.pIdField;
  }

  public PathTable setPIdField(String pIdField) {
    this.pIdField = pIdField;
    return this;
  }

  public String getPathField() {
    return this.pathField == null ? "PATH" : this.pathField;
  }

  public PathTable setPathField(String pathField) {
    this.pathField = pathField;
    return this;
  }

  public String getNameField() {
    return this.nameField == null ? "GNMK_MC" : this.nameField;
  }

  public PathTable setNameField(String nameField) {
    this.nameField = nameField;
    return this;
  }
}
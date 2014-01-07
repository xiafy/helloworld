package org.webframework.mvc.wrapper;

public class WebMvcConfig
{
  public static final String KEY_VIEW = "_view";
  public static final String KEY_EXCEPTION = "_exception";
  public static final String KEY_JSP_PATH = "_jsp_path";
  private boolean processUploadFiles;
  private String defaultPackagePrefix;
  private String excludePackagePrefix;
  private String commandRequestExt;
  private String commandMethodParameterName;
  private String defaultCommandMethod;
  private String errorPage;
  private String ajaxDebugParameterKey;
  private String ajaxErrorParameterKey;
  private String ajaxDebugPage;
  private String ajaxErrorPage;
  private String characterEncoding;

  public String getDefaultPackagePrefix()
  {
    return this.defaultPackagePrefix;
  }

  public boolean isProcessUploadFiles() {
    return this.processUploadFiles;
  }

  public void setProcessUploadFiles(boolean processUploadFiles) {
    this.processUploadFiles = processUploadFiles;
  }

  public void setDefaultPackagePrefix(String defaultPackagePrefix) {
    this.defaultPackagePrefix = defaultPackagePrefix;
  }

  public String getExcludePackagePrefix() {
    return this.excludePackagePrefix;
  }

  public void setExcludePackagePrefix(String excludePackagePrefix) {
    this.excludePackagePrefix = excludePackagePrefix;
  }

  public String getCommandMethodParameterName() {
    return this.commandMethodParameterName;
  }

  public void setCommandMethodParameterName(String commandMethodParameterName) {
    this.commandMethodParameterName = commandMethodParameterName;
  }

  public String getErrorPage() {
    return this.errorPage;
  }

  public void setErrorPage(String errorPage) {
    this.errorPage = errorPage;
  }

  public String getCommandRequestExt() {
    return this.commandRequestExt;
  }

  public void setCommandRequestExt(String commandRequestExt) {
    this.commandRequestExt = commandRequestExt;
  }

  public String getDefaultCommandMethod() {
    return this.defaultCommandMethod;
  }

  public void setDefaultCommandMethod(String defaultCommandMethod) {
    this.defaultCommandMethod = defaultCommandMethod;
  }

  public String getAjaxDebugPage() {
    return this.ajaxDebugPage;
  }

  public void setAjaxDebugPage(String ajaxDebugPage) {
    this.ajaxDebugPage = ajaxDebugPage;
  }

  public String getAjaxErrorPage() {
    return this.ajaxErrorPage;
  }

  public void setAjaxErrorPage(String ajaxErrorPage) {
    this.ajaxErrorPage = ajaxErrorPage;
  }

  public String getAjaxDebugParameterKey() {
    return this.ajaxDebugParameterKey;
  }

  public void setAjaxDebugParameterKey(String ajaxDebugParameterKey) {
    this.ajaxDebugParameterKey = ajaxDebugParameterKey;
  }

  public String getCharacterEncoding() {
    return this.characterEncoding;
  }

  public void setCharacterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
  }

  public String getAjaxErrorParameterKey() {
    return this.ajaxErrorParameterKey;
  }

  public void setAjaxErrorParameterKey(String ajaxErrorParameterKey) {
    this.ajaxErrorParameterKey = ajaxErrorParameterKey;
  }
}
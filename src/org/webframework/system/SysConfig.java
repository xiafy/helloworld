package org.webframework.system;

import org.webframework.system.login.auth.algorithm.IEncrypt;

public class SysConfig
{
  public static final String KEY_SESSION_MAIN = "_Web_key_main";
  public static final String KEY_USER = "_Web_key_user";
  public static final String KEY_REQUEST_CONTEXT = "_Web_key_request_context";
  public static final String KEY_CHECK_CODE = "_Web_key_check_code";
  public static final String KEY_LOGIN_FAIL_MSG = "_Web_key_login_fail_msg";
  public static final String KEY_NO_PERMIT_PATH = "_Web_key_no_permit_path";
  public static final String KEY_USER_AVATAR = "_Web_key_user_avatar";
  public static final String KEY_IS_CMD = "_Web_key_is_cmd";
  public static final String KEY_CMD_NAME = "command";
  public static final String CacheSplit = "_";
  public static final String CacheAcl = "_Web_key_cache_acl";
  public static final String CacheAclMenuRole = "_Web_key_cache_acl_MenuRole";
  public static final String CacheAclMenuAnony = "_Web_key_cache_acl_MenuRole_AnonyMenuRes";
  public static final String CacheAclResAnony = "_Web_key_cache_acl_AnonyRes";
  public static final String CacheAclOrgan = "_Web_key_cache_acl_Organ";
  public static final String CacheLog = "_Web_key_cache_log";
  public static final String KEY_LOG_COLLECTION = "_Web_key_cache_log_collection";
  public static final String KEY_LOG_RES_CONFIG = "_Web_key_cache_log_res_config";
  public static final String CacheInitConfig = "_Web_key_cache_init_config";
  public static final String KEY_INIT_CONFIG_SYSTEM = "_Web_key_cache_init_config_system";
  public static final String KEY_INIT_CONFIG_USER = "_Web_key_cache_init_config_user";
  private String loginPage;
  private String loginTheme;
  private String indexPage;
  private String noPermitPage;
  private IEncrypt passwordEncrypt;
  private String especialCommand;

  public String getLoginPage()
  {
    return this.loginPage;
  }
  public void setLoginPage(String loginPage) {
    this.loginPage = loginPage;
  }
  public String getIndexPage() {
    return this.indexPage;
  }
  public void setIndexPage(String indexPage) {
    this.indexPage = indexPage;
  }
  public String getNoPermitPage() {
    return this.noPermitPage;
  }
  public void setNoPermitPage(String noPermitPage) {
    this.noPermitPage = noPermitPage;
  }
  public String getEspecialCommand() {
    return this.especialCommand;
  }
  public void setEspecialCommand(String especialCommand) {
    this.especialCommand = especialCommand;
  }
  public IEncrypt getPasswordEncrypt() {
    return this.passwordEncrypt;
  }
  public void setPasswordEncrypt(IEncrypt passwordEncrypt) {
    this.passwordEncrypt = passwordEncrypt;
  }
  public String getLoginTheme() {
    return this.loginTheme;
  }
  public void setLoginTheme(String loginTheme) {
    this.loginTheme = loginTheme;
  }
}
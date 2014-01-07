package org.webframework.system.login.auth.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.webframework.BeanFactory;
import org.webframework.mvc.View;
import org.webframework.system.SysCommon;
import org.webframework.system.SysConfig;
import org.webframework.system.login.User;
import org.webframework.system.login.auth.ILoginAuth;
import org.webframework.system.login.auth.algorithm.IEncrypt;
import org.webframework.system.manage.entries.SysOrganBean;

public class BaseAuth
  implements ILoginAuth
{
  private User user;
  private String czryDm;
  private String errorMsg;

  public boolean validate(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    this.czryDm = view.getString("czryDm");

    if ((this.czryDm == null) || ("".equals(this.czryDm.trim()))) {
      this.errorMsg = "登录账号不能为空";
      return false;
    }
    this.czryDm = this.czryDm.toUpperCase();

    view.put("encryptMode", "false");
    String encryptMode = view.getString("encryptMode");
    String encryptPassword = view.getString("encryptPassword");
    String password = view.getString("password");
    if ("false".equals(encryptMode)) {
      if ((password == null) || ("".equals(password))) {
        this.errorMsg = "密码不能为空";
        return false;
      }
    }
    else if ((encryptPassword == null) || ("".equals(encryptPassword))) {
      this.errorMsg = "密码不能为空";
      return false;
    }

    this.user = SysCommon.getUserById(this.czryDm);
    if (this.user == null) {
      this.errorMsg = "用户不存在";
      return false;
    }

    if ("false".equals(encryptMode)) {
      IEncrypt passwordEncrypt = ((SysConfig)BeanFactory.getBean("sysConfig")).getPasswordEncrypt();
      if (!passwordEncrypt.encode(password).equals(this.user.getPassword())) {
        this.errorMsg = "密码不正确";
        return false;
      }
    }
    else if (!encryptPassword.equals(this.user.getPassword())) {
      this.errorMsg = "密码不正确";
      return false;
    }

    if (!"01".equals(this.user.getZhzt())) {
      this.errorMsg = ("用户状态为非正常，代码：" + this.user.getZhzt());
      return false;
    }
    int count = SysCommon.getCzryOnlineCount(this.czryDm);
    if ((this.user.getZdhhs() != -1) && (this.user.getZdhhs() <= count)) {
      this.errorMsg = ("该用户已达到最大会话数[" + this.user.getZdhhs() + "],不能登录");
      return false;
    }

    handleUserOrganInfromation(this.user);
    return true;
  }

  private void handleUserOrganInfromation(User user)
    throws Exception
  {
    String jgLxDm = user.getJgLxDm();
    String jgDm = user.getJgDm();
    SysOrganBean aclOrganBean = null;
    if ("01".equals(jgLxDm))
    {
      user.setDepartmentId(null);
      user.setDepartmentName(null);

      aclOrganBean = SysCommon.getParentOrganById(jgDm);
      if (aclOrganBean != null) {
        user.setSjJgDm(aclOrganBean.getJgDm());
        user.setSjJgMc(aclOrganBean.getJgMc());
      }
    } else if ("02".equals(jgLxDm))
    {
      user.setDepartmentId(user.getJgDm());
      user.setDepartmentName(user.getJgMc());
      user.setJgDm(null);
      user.setJgMc(null);

      aclOrganBean = SysCommon.getParentOrganById(jgDm);
      if (aclOrganBean != null) {
        user.setJgDm(aclOrganBean.getJgDm());
        user.setJgMc(aclOrganBean.getJgMc());

        jgDm = user.getJgDm();
        aclOrganBean = SysCommon.getParentOrganById(jgDm);
        if (aclOrganBean != null) {
          user.setSjJgDm(aclOrganBean.getJgDm());
          user.setSjJgMc(aclOrganBean.getJgMc());
        }
      }
    }
  }

  public User getUser() {
    return this.user;
  }
  public String getErrorMsg() {
    return this.errorMsg;
  }
  public String getCzryDm() {
    return this.czryDm;
  }
}
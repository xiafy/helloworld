package org.webframework.system.login.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.webframework.mvc.View;
import org.webframework.system.login.User;

public abstract interface ILoginAuth
{
  public abstract boolean validate(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, View paramView)
    throws Exception;

  public abstract String getErrorMsg();

  public abstract User getUser();

  public abstract String getCzryDm();
}
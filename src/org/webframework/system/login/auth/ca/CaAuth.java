package org.webframework.system.login.auth.ca;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.webframework.mvc.View;
import org.webframework.system.login.User;
import org.webframework.system.login.auth.ILoginAuth;

public class CaAuth
  implements ILoginAuth
{
  public String getErrorMsg()
  {
    return null;
  }

  public User getUser() {
    return null;
  }

  public boolean validate(HttpServletRequest request, HttpServletResponse response, View view) throws Exception
  {
    return false;
  }

  public String getCzryDm() {
    return null;
  }
}
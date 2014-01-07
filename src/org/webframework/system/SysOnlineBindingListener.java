package org.webframework.system;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class SysOnlineBindingListener
  implements HttpSessionBindingListener
{
  String czryDm;

  public SysOnlineBindingListener(String czryDm)
  {
    this.czryDm = czryDm;
  }

  public void valueBound(HttpSessionBindingEvent event) {
    HttpSession session = event.getSession();
    try {
      SysCommon.deleteSysOnline(session.getId());
      SysCommon.insertSysOnline(this.czryDm, session.getId());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void valueUnbound(HttpSessionBindingEvent event)
  {
  }
}
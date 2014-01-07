package org.webframework.system;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.webframework.system.log.track.TrackLogin;

public class SysSessionListener
  implements HttpSessionListener
{
  public void sessionCreated(HttpSessionEvent event)
  {
  }

  public void sessionDestroyed(HttpSessionEvent event)
  {
    try
    {
      SysCommon.deleteSysOnline(event.getSession().getId());
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    TrackLogin.trackSessionDestroyed(event.getSession());
  }
}
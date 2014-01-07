package org.webframework.system;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SysSessionServlet extends HttpServlet
{
  public void init()
    throws ServletException
  {
    try
    {
      SysCommon.deleteSysOnline();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
  }
}
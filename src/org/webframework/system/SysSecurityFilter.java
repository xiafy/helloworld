package org.webframework.system;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SysSecurityFilter
  implements Filter
{
  public void destroy()
  {
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws IOException, ServletException
  {
    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)resp;
    String header = request.getHeader("Referer");
    String basePathUrl = request.getScheme() + "://" + 
      request.getServerName();
    if (header != null)
      chain.doFilter(req, resp);
    else
      return;
  }

  public void init(FilterConfig arg0)
    throws ServletException
  {
  }
}
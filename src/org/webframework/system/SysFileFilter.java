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
import org.webframework.BeanFactory;

public class SysFileFilter
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
    String servletPath = request.getServletPath();
    String header = request.getHeader("Referer");
    String basePathUrl = request.getScheme() + "://" + 
      request.getServerName();
    if ("command".equals(
      request.getAttribute("_Web_key_is_cmd"))) {
      chain.doFilter(req, resp);
      return;
    }
    if (header != null) {
      chain.doFilter(req, resp);
      return;
    }

    if (servletPath.contains(((SysConfig)BeanFactory.getBean("sysConfig")).getLoginTheme())) {
      chain.doFilter(req, resp);
      return;
    }
  }

  public void init(FilterConfig cfg)
    throws ServletException
  {
  }
}
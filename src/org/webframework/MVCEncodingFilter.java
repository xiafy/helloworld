package org.webframework;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.mvc.WebMvcConfig;

public class MVCEncodingFilter
  implements Filter
{
  private static Log log = LogFactory.getLog(MVCEncodingFilter.class);

  protected String encoding = null;

  public void init(FilterConfig filterConfig) throws ServletException
  {
  }

  public void destroy() {
    this.encoding = null;
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    HttpServletRequest req = (HttpServletRequest)request;

    String ajaxHeader = req.getHeader("X-Requested-With");
    if ((ajaxHeader != null) && (ajaxHeader.equalsIgnoreCase("XMLHttpRequest"))) {
      request.setCharacterEncoding("utf-8");
    }
    else if (request.getCharacterEncoding() == null) {
      String encoding = getEncoding();
      if (encoding != null) {
        request.setCharacterEncoding(encoding);
        response.setContentType("text/html; charset=" + encoding);
      }
    }

    chain.doFilter(request, response);
  }

  protected String getEncoding() {
    if (this.encoding == null) {
      this.encoding = 
        ((WebMvcConfig)BeanFactory.getBean("webMvcConfig")).getCharacterEncoding();
    }
    return this.encoding;
  }
}
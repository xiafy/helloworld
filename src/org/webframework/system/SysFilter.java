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
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.BeanFactory;
import org.webframework.WMap;
import org.webframework.system.log.track.TrackBase;
import org.webframework.system.login.User;
import org.webframework.system.login.bean.RequestContext;
import org.webframework.system.manage.cache.MenuCacheService;

public class SysFilter
  implements Filter
{
  protected static Log log = LogFactory.getLog(SysFilter.class);

  private boolean isProduceMode = true;

  public void init(FilterConfig config) throws ServletException {
    if ("false".equalsIgnoreCase(config.getInitParameter("produceMode")))
      this.isProduceMode = false;
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
  {
    HttpServletRequest req = (HttpServletRequest)request;
    HttpSession session = req.getSession();

    String requestUrl = getRequestUrl(req);
    String cmdClass = getCommandNamePostFix(requestUrl);
    log.debug("请求路径：" + requestUrl);

    User user = SysContext.getSysUser(session);
    if (user != null) {
      SysContext.setSysUser(user);
    }

    RequestContext requestContext = new RequestContext();
    SysContext.setRequestContext(req, requestContext);
    SysContext.setRequestContext(requestContext);
    requestContext.setRequestUrl(cmdClass);
    if (user != null) {
      requestContext.setCzryDm(user.getCzryDm());
    }

    if (user != null) {
      WMap userConfig = SysContext.getUserConfig(session);
      if (userConfig == null) {
        userConfig = SysContext.refreshUserConfig(session);
      }
      SysContext.setUserConfig(userConfig);
    }

    SysConfig config = (SysConfig)BeanFactory.getBean("sysConfig");
    boolean isEspecial = isEspecialCommand(config, cmdClass);
    if (!isEspecial) {
      MenuCacheService menuCacheService = new MenuCacheService();
      boolean isAnonyRes = false;
      try {
        isAnonyRes = menuCacheService.nmfwBzRes(cmdClass, requestContext);
      } catch (Exception e) {
        log.error("AclFilter检测资源是否可匿名访问时时产生了异常", e);
      }
      if (!isAnonyRes) {
        if (user == null) {
          TrackBase.track(req);
          ((HttpServletResponse)response).sendRedirect(config.getLoginPage());
          return;
        }
        if (this.isProduceMode) {
          boolean isAuthAccess = false;
          try {
            isAuthAccess = menuCacheService.isAuthAccess(cmdClass, user, requestContext);
          } catch (Exception e) {
            log.error("调用AclFilter时产生了异常", e);
          }

          if (!isAuthAccess) {
            TrackBase.track(req);
            log.debug("没有权限：" + cmdClass);
            session.setAttribute("_Web_key_no_permit_path", cmdClass);
            ((HttpServletResponse)response).sendRedirect(config.getNoPermitPage());
            return;
          }
        }
      }
    }
    TrackBase.track(req);
    chain.doFilter(request, response);
  }

  public void destroy()
  {
  }

  protected String getCommandName(String name)
  {
    int beginIdx = name.lastIndexOf("/");
    int endIdx = name.lastIndexOf(".");
    return name.substring(beginIdx == -1 ? 0 : beginIdx + 1, 
      endIdx == -1 ? name.length() : endIdx);
  }
  protected String getRequestUrl(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    String queryString = request.getQueryString();
    return request.getServletPath() + (
      pathInfo != null ? pathInfo : "") + (
      (queryString != null) && (!queryString.trim().equals("")) ? "?" + queryString : "");
  }

  protected String getCommandNamePostFix(String name) {
    int beginIdx = name.lastIndexOf("/");
    return name.substring(beginIdx == -1 ? 0 : beginIdx + 1);
  }

  public boolean isEspecialCommand(SysConfig config, String cmdClass) {
    String especialCommands = config.getEspecialCommand();
    if ((especialCommands != null) && (!"".equals(especialCommands))) {
      String[] especialClazz = config.getEspecialCommand().split(",");
      for (int i = 0; i < especialClazz.length; i++) {
        if (cmdClass.indexOf(especialClazz[i]) != -1)
        {
          return true;
        }
      }
    }
    return false;
  }
}
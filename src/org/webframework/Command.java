package org.webframework;

import inspur.tax.log.LogUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.exception.WebMvcException;
import org.webframework.mvc.AjaxView;
import org.webframework.mvc.View;
import org.webframework.mvc.WebMvcConfig;
import org.webframework.mvc.wrapper.ajax.WebAjaxJSONResponseWrapper;
import org.webframework.mvc.wrapper.ajax.WebAjaxResponseWrapper;
import org.webframework.mvc.wrapper.ajax.WebAjaxXMLResponseWrapper;
import org.webframework.system.log.track.TrackException;
import org.webframework.tag.grid.Grid;
import org.webframework.tag.grid.exp.ExpGrid;

public class Command
{
  protected static Log log = LogFactory.getLog(Command.class);

  public Object execute(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    if (processDebug(request, response, view)) return view;
    WebMvcConfig config = (WebMvcConfig)BeanFactory.getBean("webMvcConfig");
    String methodName = view.getString(config.getCommandMethodParameterName());

    if ((methodName == null) || ("".equals(methodName))) {
      methodName = config.getDefaultCommandMethod();
    }
    Method method = null;
    String expGridName = view.getString("_grid_key_exp_gridname");

    if ((expGridName != null) && (!"".equals(expGridName))) {
      Grid grid = null;
      method = getCommonMethod(methodName);
      if (method != null) {
        method.invoke(this, new Object[] { request, response, view });
        grid = (Grid)view.get(expGridName);
      } else {
        method = getAjaxMethod(methodName);
        if (method != null) {
          AjaxView ajaxView = new AjaxView(view);
          method.invoke(this, new Object[] { request, response, ajaxView });
          grid = (Grid)ajaxView.get(expGridName);
        }
        else {
          throw new WebMvcException("未能找到适当的方法处理请求：" + getClass().getName() + " , " + methodName);
        }
      }

      ExpGrid.exp(request, response, grid);
      return view;
    }

    method = getCommonMethod(methodName);
    if (method != null) {
      request.setAttribute("_Web_key_is_cmd", "command");
      String nextJspPath = (String)method.invoke(this, new Object[] { request, response, view });
      view.setNextJspPath(nextJspPath);
      return view;
    }

    method = getAjaxMethod(methodName);
    if (method != null) {
      WebAjaxResponseWrapper wrapper = null;
      String responseType = request.getHeader("Response-Type");
      if ("xml".equalsIgnoreCase(responseType))
        wrapper = new WebAjaxXMLResponseWrapper();
      else
        wrapper = new WebAjaxJSONResponseWrapper();
      try
      {
        AjaxView ajaxView = new AjaxView(view);
        method.invoke(this, new Object[] { request, response, ajaxView });

        TrackException.trackAjaxWarn(request, ajaxView);
        return wrapper.getResponse(ajaxView);
      } catch (Exception e) {
        log.error("执行请求时产生了异常:" + e.getMessage(), e);
        try
        {
          LogUtil.addLogYwcz(request, e, "", "00", "0");
        } catch (Exception ee) {
          log.error("保存异常日志发送错误", ee);
        }

        return wrapper.getException(e);
      }

    }

    throw new WebMvcException("未能找到适当的方法处理请求：" + getClass().getName() + " , " + methodName);
  }

  public boolean processDebug(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    WebMvcConfig mvcConfig = (WebMvcConfig)BeanFactory.getBean("webMvcConfig");
    if ("true".equals(view.getString(mvcConfig.getAjaxDebugParameterKey()))) {
      view.setNextJspPath(mvcConfig.getAjaxDebugPage());
      return true;
    }
    if ("true".equals(view.getString(mvcConfig.getAjaxErrorParameterKey()))) {
      view.setNextJspPath(mvcConfig.getAjaxErrorPage());
      return true;
    }
    return false;
  }

  public Method getCommonMethod(String methodName)
  {
    Method method = getAccessibleMethod(getClass(), methodName, 
      new Class[] { HttpServletRequest.class, HttpServletResponse.class, View.class });
    if ((method != null) && (String.class.equals(method.getReturnType()))) {
      return method;
    }
    return null;
  }

  public Method getAjaxMethod(String methodName)
  {
    return getAccessibleMethod(getClass(), methodName, 
      new Class[] { HttpServletRequest.class, HttpServletResponse.class, AjaxView.class });
  }

  public static Method getAccessibleMethod(Class clazz, String methodName, Class parameterType)
  {
    Class[] parameterTypes = { parameterType };
    return getAccessibleMethod(clazz, methodName, parameterTypes);
  }

  public static Method getAccessibleMethod(Class clazz, String methodName, Class[] parameterTypes)
  {
    try
    {
      return getAccessibleMethod(clazz.getMethod(methodName, 
        parameterTypes)); } catch (NoSuchMethodException e) {
    }
    return null;
  }

  public static Method getAccessibleMethod(Method method)
  {
    if (method == null) {
      return null;
    }
    if (!Modifier.isPublic(method.getModifiers())) {
      return null;
    }
    Class clazz = method.getDeclaringClass();
    if (Modifier.isPublic(clazz.getModifiers())) {
      return method;
    }
    method = getAccessibleMethodFromInterfaceNest(clazz, method.getName(), 
      method.getParameterTypes());
    return method;
  }

  private static Method getAccessibleMethodFromInterfaceNest(Class clazz, String methodName, Class[] parameterTypes)
  {
    Method method = null;
    for (; clazz != null; clazz = clazz.getSuperclass()) {
      Class[] interfaces = clazz.getInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
        if (!Modifier.isPublic(interfaces[i].getModifiers()))
          continue;
        try
        {
          method = interfaces[i].getDeclaredMethod(methodName, 
            parameterTypes);
        } catch (NoSuchMethodException localNoSuchMethodException) {
        }
        if (method != null) {
          break;
        }
        method = getAccessibleMethodFromInterfaceNest(interfaces[i], 
          methodName, parameterTypes);
        if (method != null) {
          break;
        }
      }
    }
    if (method != null) {
      return method;
    }
    return null;
  }
}
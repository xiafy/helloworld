package org.webframework.mvc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.webframework.BeanFactory;
import org.webframework.Command;
import org.webframework.exception.WebMvcException;
import org.webframework.mvc.wrapper.fileupload.AbstractMultipartHttpServletRequest;
import org.webframework.mvc.wrapper.fileupload.CommonsMultipartResolver;
import org.webframework.mvc.wrapper.fileupload.MultipartResolver;
import org.webframework.tag.grid.Grid;
import org.webframework.tag.grid.GridParams;

public class WebCommandProcessor
{
  public View wrapView(HttpServletRequest request)
    throws WebMvcException
  {
    WebMvcConfig config = (WebMvcConfig)BeanFactory.getBean("webMvcConfig");
    View view = new View();

    if (config.isProcessUploadFiles()) {
      request = wrapMultipartRequest(request);

      if ((request instanceof AbstractMultipartHttpServletRequest)) {
        view.setMultipartFiles(((AbstractMultipartHttpServletRequest)request).getFileMap());
      }
    }

    populateMap(view, request.getParameterMap());
    return view;
  }

  public Object processRequest(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    View view = wrapView(request);
    String CommandName = getCommandName(request.getServletPath());

    Command command = WebCommandFactory.getInstance().getCommand(CommandName);
    return command.execute(request, response, view);
  }

  protected String getCommandName(String name)
  {
    int beginIdx = name.lastIndexOf("/");
    int endIdx = name.lastIndexOf(".");
    return name.substring(beginIdx == -1 ? 0 : beginIdx + 1, 
      endIdx == -1 ? name.length() : endIdx);
  }

  protected HttpServletRequest wrapMultipartRequest(HttpServletRequest request)
    throws WebMvcException
  {
    MultipartResolver resolver = new CommonsMultipartResolver();
    if (resolver.isMultipart(request)) {
      return resolver.resolveMultipart(request);
    }
    return request;
  }

  protected void populateMap(Map view, Map parameterMap)
  {
    Iterator iter = parameterMap.entrySet().iterator();
    List params = new ArrayList();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      Object key = entry.getKey();
      Object value = entry.getValue();
      if (value == null) {
        view.put(key, null);
      }
      else if (value.getClass().isArray()) {
        int length = ((Object[])value).length;
        if (length == 1)
          view.put(key, ((Object[])value)[0]);
        else
          view.put(key, value);
      }
      else {
        view.put(key, value);
      }

      if (((String)key).indexOf("_grid_params_") == 0) {
        params.add(key);
      }
    }
    wrapGridData((View)view, params);
  }

  private void wrapGridData(View view, List params)
  {
    int size = params.size();
    GridParams param = null;
    for (int i = 0; i < size; i++) {
      param = new GridParams((String)params.get(i), view);
      view.put(param.getName(), new Grid(param));
    }
  }
}
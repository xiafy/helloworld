package org.webframework.mvc.wrapper.ajax;

import javax.servlet.http.HttpServletResponse;
import org.webframework.exception.WebMvcException;
import org.webframework.mvc.AjaxView;

public abstract class WebAjaxResponseWrapper
{
  private HttpServletResponse response;

  public void init(HttpServletResponse response)
  {
    this.response = response;
  }

  protected HttpServletResponse getResponse() throws WebMvcException {
    if (this.response == null) throw new WebMvcException("HttpServletResponse为空，请使用init方法为其赋值");
    return this.response;
  }

  public abstract void doResponse(AjaxView paramAjaxView)
    throws WebMvcException;

  public abstract void doException(Throwable paramThrowable)
    throws Exception;

  public abstract String getResponse(AjaxView paramAjaxView)
    throws WebMvcException;

  public abstract String getException(Throwable paramThrowable)
    throws Exception;
}
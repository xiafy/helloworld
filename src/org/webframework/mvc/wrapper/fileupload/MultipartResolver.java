package org.webframework.mvc.wrapper.fileupload;

import javax.servlet.http.HttpServletRequest;
import org.webframework.exception.WebMvcException;

public abstract interface MultipartResolver
{
  public abstract boolean isMultipart(HttpServletRequest paramHttpServletRequest);

  public abstract MultipartHttpServletRequest resolveMultipart(HttpServletRequest paramHttpServletRequest)
    throws WebMvcException;

  public abstract void cleanupMultipart(MultipartHttpServletRequest paramMultipartHttpServletRequest);
}
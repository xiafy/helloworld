package org.webframework.mvc.wrapper.fileupload;

import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public abstract interface MultipartHttpServletRequest extends HttpServletRequest
{
  public abstract Iterator getFileNames();

  public abstract MultipartFile getFile(String paramString);

  public abstract Map getFileMap();
}
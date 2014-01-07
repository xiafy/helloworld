package org.webframework.mvc.wrapper.fileupload;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractMultipartHttpServletRequest extends HttpServletRequestWrapper
  implements MultipartHttpServletRequest
{
  protected final Log logger = LogFactory.getLog(getClass());
  private Map multipartFiles;

  protected AbstractMultipartHttpServletRequest(HttpServletRequest request)
  {
    super(request);
  }

  protected void setMultipartFiles(Map multipartFiles) {
    this.multipartFiles = multipartFiles;
  }

  public Iterator getFileNames() {
    return this.multipartFiles.keySet().iterator();
  }

  public MultipartFile getFile(String name) {
    return (MultipartFile)this.multipartFiles.get(name);
  }

  public Map getFileMap() {
    return this.multipartFiles;
  }
}
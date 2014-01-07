package org.webframework.mvc.wrapper.fileupload;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.webframework.exception.WebMvcException;

public class CommonsMultipartResolver
  implements MultipartResolver
{
  protected final Log logger = LogFactory.getLog(getClass());
  private DiskFileUpload fileUpload;

  public CommonsMultipartResolver()
  {
    this.fileUpload = newFileUpload();
  }

  public CommonsMultipartResolver(ServletContext servletContext) {
    this();
    this.fileUpload.setRepositoryPath(((File)
      servletContext.getAttribute("javax.servlet.context.tempdir")).getAbsolutePath());
  }

  protected DiskFileUpload newFileUpload() {
    return new DiskFileUpload();
  }

  public DiskFileUpload getFileUpload() {
    return this.fileUpload;
  }

  public void setMaximumFileSize(long maximumFileSize) {
    this.fileUpload.setSizeMax(maximumFileSize);
  }

  public void setMaximumInMemorySize(int maximumInMemorySize) {
    this.fileUpload.setSizeThreshold(maximumInMemorySize);
  }

  public void setHeaderEncoding(String headerEncoding) {
    this.fileUpload.setHeaderEncoding(headerEncoding);
  }

  public boolean isMultipart(HttpServletRequest request) {
    return FileUploadBase.isMultipartContent(request);
  }

  public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws WebMvcException
  {
    try {
      List fileItems = this.fileUpload.parseRequest(request);
      Map parameters = new HashMap();
      Map multipartFiles = new HashMap();
      for (Iterator i = fileItems.iterator(); i.hasNext(); ) {
        FileItem fileItem = (FileItem)i.next();
        if (fileItem.isFormField()) {
          String[] curParam = (String[])parameters.get(fileItem.getFieldName());
          if (curParam == null) {
            parameters.put(fileItem.getFieldName(), new String[] { fileItem.getString() });
          }
          else {
            String[] newParam = StringUtils.addStringToArray(curParam, fileItem.getString());
            parameters.put(fileItem.getFieldName(), newParam);
          }
        }
        else {
          CommonsMultipartFile file = new CommonsMultipartFile(fileItem);
          multipartFiles.put(file.getName(), file);
          if (this.logger.isDebugEnabled()) {
            this.logger.debug("解析到上传文件：\n表单域： " + 
              file.getName() + 
              "\n文件大小：" + file.getSize() + 
              "\n文件名：" + file.getOriginalFileName() + 
              "\n存储区域" + file.getStorageDescription());
          }
        }

      }

      parameters.putAll(request.getParameterMap());
      return new DefaultMultipartHttpServletRequest(request, multipartFiles, parameters);
    } catch (FileUploadException ex) {
    	 throw new WebMvcException("使用Apache Common File Upload包解析上传文件时产生异常", ex);
    }
  }

  public void cleanupMultipart(MultipartHttpServletRequest request)
  {
    Map multipartFiles = request.getFileMap();
    for (Iterator i = multipartFiles.keySet().iterator(); i.hasNext(); ) {
      String name = (String)i.next();
      CommonsMultipartFile file = (CommonsMultipartFile)multipartFiles.get(name);
      this.logger.debug("删除上传文件：\n表单域：" + 
        file.getName() + 
        "\n文件名：" + file.getOriginalFileName() + 
        "\n存储区域：" + file.getStorageDescription());
      file.getFileItem().delete();
    }
  }
}
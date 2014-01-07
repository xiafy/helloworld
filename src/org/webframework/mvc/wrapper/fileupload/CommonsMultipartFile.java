package org.webframework.mvc.wrapper.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.fileupload.DefaultFileItem;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonsMultipartFile
  implements MultipartFile
{
  protected final Log logger = LogFactory.getLog(getClass());
  private final FileItem fileItem;

  protected CommonsMultipartFile(FileItem fileItem)
  {
    this.fileItem = fileItem;
  }

  public FileItem getFileItem() {
    return this.fileItem;
  }

  public String getName() {
    return this.fileItem.getFieldName();
  }

  public String getOriginalFileName() {
    return new File(this.fileItem.getName()).getName();
  }

  public String getContentType() {
    return this.fileItem.getContentType();
  }

  public long getSize() {
    return this.fileItem.getSize();
  }

  public byte[] getBytes() {
    return this.fileItem.get();
  }

  public InputStream getInputStream() throws IOException {
    return this.fileItem.getInputStream();
  }

  public void saveAs(File dest) throws IOException, IllegalStateException {
    try {
      this.fileItem.write(dest);
      this.logger.debug("上传文件 被转存至另一区域:\n文件名：" + 
        getOriginalFileName() + 
        "\n表单域：" + getName() + 
        "\n原位置：" + getStorageDescription() + 
        "\n新位置：" + dest.getAbsolutePath());
    } catch (Exception ex) {
      this.logger.error("无法将文件另存至另一位置：\n文件名：" + 
        getOriginalFileName() + 
        "\n表单域：" + getName() + 
        "\n原位置：" + getStorageDescription() + 
        "\n新位置：" + dest.getAbsolutePath(), ex);
      throw new IOException("无法将文件另存至另一位置：\n文件名：" + 
        getOriginalFileName() + 
        "\n表单域：" + getName() + 
        "\n原位置：" + getStorageDescription() + 
        "\n新位置：" + dest.getAbsolutePath() + 
        "\n异常信息：" + ex.getMessage());
    }
  }

  protected String getStorageDescription() {
    if (this.fileItem.isInMemory())
      return "内存";
    if ((this.fileItem instanceof DefaultFileItem)) {
      return " [" + ((DefaultFileItem)this.fileItem).getStoreLocation().getAbsolutePath() + "]";
    }
    return "硬盘";
  }
}
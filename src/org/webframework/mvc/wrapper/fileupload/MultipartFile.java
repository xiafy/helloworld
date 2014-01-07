package org.webframework.mvc.wrapper.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract interface MultipartFile
{
  public abstract String getName();

  public abstract String getOriginalFileName();

  public abstract String getContentType();

  public abstract long getSize();

  public abstract byte[] getBytes()
    throws IOException;

  public abstract InputStream getInputStream()
    throws IOException;

  public abstract void saveAs(File paramFile)
    throws IOException, IllegalStateException;
}
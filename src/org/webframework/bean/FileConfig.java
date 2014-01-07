package org.webframework.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.webframework.bean.beans.factory.xml.XmlBeanFactory;

public class FileConfig
  implements BeanConfig
{
  private static Log log = LogFactory.getLog(FileConfig.class);
  public static String FILE_SEPARATOR = System.getProperty("file.separator");
  private String[] files;

  public String[] getFiles()
  {
    return this.files;
  }

  private String transform(String s) {
    if (FILE_SEPARATOR.equals("\\"))
      return s.replace('/', '\\');
    if (FILE_SEPARATOR.equals("/")) {
      return s.replace('\\', '/');
    }
    return s;
  }

  public void loadBeanConfig(AbstractXmlApplicationContext ctx)
  {
    XmlBeanFactory factory = (XmlBeanFactory)ctx.getBeanFactory();
    String prefix = "/";
    if ((prefix.endsWith("/")) || (prefix.endsWith("\\"))) {
      prefix = prefix.substring(0, prefix.length() - 1);
    }
    for (int i = 0; i < this.files.length; i++) {
      String res = this.files[i];
      if (!res.startsWith("/")) {
        res = "/" + res;
      }
      String file = transform(prefix + res);
      try
      {
        factory.loadBeanDefinitions(file);

        if (log.isInfoEnabled())
          log.info("loaded : " + file);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        log.error("loaded with exception : " + file, e);
      }
    }
  }

  public void setFiles(String[] files) {
    this.files = files;
  }

  public void afterPropertiesSet()
    throws Exception
  {
  }
}
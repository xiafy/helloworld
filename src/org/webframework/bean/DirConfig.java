package org.webframework.bean;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.webframework.util.SelectorUtils;

public class DirConfig
  implements InitializingBean, BeanConfig
{
  private static Log log = LogFactory.getLog(DirConfig.class);

  public static String FILE_SEPARATOR = System.getProperty("file.separator");
  private String dir = null;
  private String[] includes = null;
  private String[] excludes = null;

  public void setDir(String dir) {
    if ((dir.startsWith("/")) || (dir.startsWith("\\"))) {
      dir = dir.substring(1);
    }
    this.dir = transform(dir);
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
    System.out.println("ccccccccccwwwwwwwwwwwwwwwc111111");
    if (this.dir == null) {
      log.error("dir attribute is not defined, skip this configuration");
      return;
    }

    String prefix = "/";
    if ((prefix.endsWith("/")) || (prefix.endsWith("\\"))) {
      prefix = prefix.substring(0, prefix.length() - 1);
    }

    if ((this.dir.endsWith("/")) || (this.dir.endsWith("\\"))) {
      this.dir = this.dir.substring(0, this.dir.length() - 1);
    }

    prefix = prefix + FILE_SEPARATOR + this.dir + FILE_SEPARATOR;
    List fileList = new ArrayList();
    File root = new File(prefix);
    if (root.exists()) {
      if (root.isDirectory()) {
        scanDir(root, fileList, prefix);
      } else {
        log.error("can not read directory " + root + ",skip this configuration");
        return;
      }
    } else {
      log.error("can not read directory " + root + ",skip this configuration");
      return;
    }

    XmlBeanDefinitionReader factory = (XmlBeanDefinitionReader)ctx.getBeanFactory();
    for (int i = 0; i < fileList.size(); i++) {
      File file = (File)fileList.get(i);
      try {
        factory.loadBeanDefinitions(file.getAbsolutePath());

        if (log.isInfoEnabled())
          log.info("loaded : " + file.getAbsolutePath());
      }
      catch (Throwable e) {
        log.error("loaded with exception : " + file.getAbsolutePath(), e);
      }
    }
  }

  private void scanDir(File dir, List fileList, String rootPath)
  {
    File[] sub = dir.listFiles();

    List subDirList = new ArrayList(sub.length);
    for (int i = 0; i < sub.length; i++) {
      if (sub[i].exists()) {
        if (sub[i].isDirectory()) {
          subDirList.add(sub[i]); } else {
          if (!sub[i].isFile())
            continue;
          boolean valid = true;
          String relativePath = sub[i].getName();
          if ((this.includes != null) && (this.includes.length > 0)) {
            valid = false;
            for (int m = 0; m < this.includes.length; m++) {
              if (!SelectorUtils.matchPath(this.includes[m], relativePath, false))
                continue;
              valid = true;
              break;
            }
          }

          if ((valid) && (this.excludes != null) && (this.excludes.length > 0)) {
            for (int n = 0; n < this.excludes.length; n++) {
              if (!SelectorUtils.matchPath(this.excludes[n], 
                relativePath, false))
                continue;
              valid = false;
              break;
            }

          }

          if (valid) {
            fileList.add(sub[i]);
          }
        }
      }
    }
    for (int j = 0; j < subDirList.size(); j++)
      scanDir((File)subDirList.get(j), fileList, rootPath);
  }

  public void setExcludes(String s)
  {
    if (s != null) {
      StringTokenizer st2 = new StringTokenizer(transform(s), ",");
      this.excludes = new String[st2.countTokens()];
      int count2 = 0;
      while (st2.hasMoreTokens())
        this.excludes[(count2++)] = st2.nextToken();
    }
  }

  public void setIncludes(String s)
  {
    if (s != null) {
      StringTokenizer st1 = new StringTokenizer(transform(s), ",");
      this.includes = new String[st1.countTokens()];
      int count1 = 0;
      while (st1.hasMoreTokens())
        this.includes[(count1++)] = st1.nextToken();
    }
  }

  public void afterPropertiesSet()
    throws Exception
  {
  }
}
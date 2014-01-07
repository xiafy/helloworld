package org.webframework.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.webframework.util.ExtendedProperties;
import org.webframework.util.SelectorUtils;

public class EnumConfig
  implements EnumService, InitializingBean, ApplicationContextAware
{
  private ApplicationContext applicationContext = null;

  public static String FILE_SEPARATOR = System.getProperty("file.separator");

  private String charSet = "gb2312";

  private static Log logger = LogFactory.getLog(EnumConfig.class);

  private Map enumsHash = new HashMap();

  private Map valuesHash = new HashMap();

  private Map descsHash = new HashMap();

  private String[] configFiles = null;

  private String dir = null;

  private String[] includesA = null;

  private String[] excludesA = null;

  private boolean isInitialized = false;

  public static String contextBasePath = "";

  public synchronized void init() {
    if (this.isInitialized) {
      return;
    }
    if (logger.isInfoEnabled())
      logger.info("EnumServiceSupport#init start");
    try
    {
      loadEnums();
      this.isInitialized = true;
      if (logger.isInfoEnabled())
        logger.info("EnumServiceSupport#init finish");
    }
    catch (Throwable e) {
      logger.info("EnumServiceSupport#init error", e);
    }
  }

  public synchronized void destroy() {
    logger.info("EnumServiceSupport#destroy start");
    this.enumsHash.clear();
    this.valuesHash.clear();
    this.descsHash.clear();
    this.isInitialized = false;
    logger.info("EnumServiceSupport#destroy finish");
  }

  private void loadEnums()
  {
    String prefix = contextBasePath;

    if ((prefix.endsWith("/")) || (prefix.endsWith("\\"))) {
      prefix = prefix.substring(0, prefix.length() - 1);
    }

    if (this.configFiles != null) {
      for (int i = 0; i < this.configFiles.length; i++) {
        InputStream input = null;
        try {
          ExtendedProperties props = new ExtendedProperties();
          if (logger.isInfoEnabled()) {
            logger.info("Load " + this.configFiles[i] + "] start");
          }
          String res = this.configFiles[i];
          if (!res.startsWith("/")) {
            res = "/" + res;
          }
          String file = transform(prefix + res);

          input = getResourceAsStream(file);
          props.load(input, "gbk");
          processProps(props);
          if (logger.isInfoEnabled())
            logger.info("Load [" + this.configFiles[i] + "] finish");
        }
        catch (Throwable e) {
          logger.error("Load [" + this.configFiles[i] + "] error", e);
          try
          {
            if (input != null)
              input.close();
          }
          catch (Throwable localThrowable1)
          {
          }
        }
        finally
        {
          try
          {
            if (input != null)
              input.close();
          }
          catch (Throwable localThrowable2)
          {
          }
        }
      }
    }
    if ((this.dir == null) || (this.dir.trim().equals(""))) {
      return;
    }
    prefix = prefix + FILE_SEPARATOR + this.dir + FILE_SEPARATOR;
    List fileList = new ArrayList();
    File root = new File(prefix);
    if (root.exists()) {
      if (root.isDirectory()) {
        scanDir(root, fileList, prefix);
      } else {
        logger.warn("It's not directory[" + prefix + "]");
        return;
      }
    } else {
      logger.warn("Directory not found[" + prefix + "]");
      return;
    }

    for (int i = 0; i < fileList.size(); i++) {
      File file = (File)fileList.get(i);
      if (logger.isInfoEnabled()) {
        logger.info("Load [" + file.getAbsolutePath() + "] start");
      }
      InputStream input = null;
      try {
        input = new FileInputStream(file);
        ExtendedProperties props = new ExtendedProperties();
        props.load(input);
        processProps(props);
        if (logger.isInfoEnabled())
          logger.info("Load [" + file.getAbsolutePath() + "] finish");
      }
      catch (Throwable e) {
        logger.error("Load [" + file.getAbsolutePath() + "] error", e);

        if (input == null) continue;
        try {
          input.close();
        }
        catch (Throwable localThrowable4)
        {
        }
      }
      finally
      {
        if (input != null)
          try {
            input.close();
          } catch (Throwable localThrowable5) {
          }
      }
    }
  }

  public final InputStream getResourceAsStream(String location) throws IOException {
    InputStream in;
    try {
      URL url = new URL(location);
      logger.debug("Opening as URL: " + location);
      return url.openStream();
    }
    catch (MalformedURLException ex)
    {
      in = getResourceByPath(location);
      if (in == null)
        throw new FileNotFoundException("Location [" + location + "] could not be opened as file path");
    }
    return in;
  }

  protected InputStream getResourceByPath(String path) throws IOException {
    return new FileInputStream(path);
  }

  private void processProps(ExtendedProperties props) {
    Enumeration keys = props.keys();
    while (keys.hasMoreElements()) {
      String enumName = (String)keys.nextElement();
      String valstring = props.getString(enumName);
      try {
        valstring = new String(valstring.getBytes("gbk"), "gbk");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      if (!this.enumsHash.containsKey(enumName)) {
        HashMap enum1 = new HashMap(5);
        List values = new ArrayList(5);
        List descs = new ArrayList(5);
        StringTokenizer st = new StringTokenizer(valstring, ",");
        while (st.hasMoreTokens()) {
          String string = st.nextToken();
          int pos = string.indexOf(":");
          if (pos > 0) {
            String val = string.substring(0, pos);
            String desc = string.substring(pos + 1);
            enum1.put(val, desc);
            values.add(val);
            descs.add(desc);
          }
        }
        this.enumsHash.put(enumName, enum1);
        this.valuesHash.put(enumName, values);
        this.descsHash.put(enumName, descs);
      } else {
        logger.warn("Can't override enum[" + enumName + "]");
      }
    }
  }

  private void scanDir(File dir, List fileList, String rootPath) {
    File[] sub = dir.listFiles();

    List subDirList = new ArrayList(sub.length);
    for (int i = 0; i < sub.length; i++) {
      if (sub[i].exists()) {
        if (sub[i].isDirectory()) {
          subDirList.add(sub[i]); } else {
          if (!sub[i].isFile())
            continue;
          boolean valid = true;
          String relativePath = sub[i].getAbsolutePath().substring(
            rootPath.length());
          if ((this.includesA != null) && (this.includesA.length > 0)) {
            valid = false;
            for (int m = 0; m < this.includesA.length; m++) {
              if (!SelectorUtils.matchPath(this.includesA[m], 
                relativePath, false))
                continue;
              valid = true;
              break;
            }
          }

          if ((valid) && (this.excludesA != null) && (this.excludesA.length > 0)) {
            for (int n = 0; n < this.excludesA.length; n++) {
              if (!SelectorUtils.matchPath(this.excludesA[n], 
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

  public String getDescByValue(String enumName, String value)
  {
    if (!this.isInitialized) {
      init();
    }
    if (this.enumsHash.containsKey(enumName)) {
      Map enum1 = (Map)this.enumsHash.get(enumName);
      if (enum1.containsKey(value)) {
        return enum1.get(value).toString();
      }
      throw new RuntimeException("Undefined enum[" + enumName + "." + 
        value + "]");
    }

    throw new RuntimeException("Undefined enum[" + enumName + "]");
  }

  public String[] getValues(String enumName)
  {
    if (!this.isInitialized) {
      init();
    }
    if (this.valuesHash.containsKey(enumName)) {
      List list = (List)this.valuesHash.get(enumName);
      return (String[])list.toArray(new String[list.size()]);
    }
    throw new RuntimeException("Undefined enum[" + enumName + "]");
  }

  public String[] getDescs(String enumName)
  {
    if (!this.isInitialized) {
      init();
    }
    if (this.descsHash.containsKey(enumName)) {
      List list = (List)this.descsHash.get(enumName);
      return (String[])list.toArray(new String[list.size()]);
    }
    throw new RuntimeException("Undefined enum[" + enumName + "]");
  }

  public Map getValueDescs(String enumName)
  {
    if (!this.isInitialized) {
      init();
    }
    if (this.enumsHash.containsKey(enumName)) {
      return (Map)this.enumsHash.get(enumName);
    }
    throw new RuntimeException("Undefined enum[" + enumName + "]");
  }

  public String[] getConfigFiles()
  {
    return this.configFiles;
  }

  public void setConfigFiles(String[] strings)
  {
    this.configFiles = strings;
  }

  public void setDir(String dir)
  {
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

  public void setExcludes(String s)
  {
    if (s != null) {
      StringTokenizer st2 = new StringTokenizer(transform(s), ",");
      this.excludesA = new String[st2.countTokens()];
      int count2 = 0;
      while (st2.hasMoreTokens())
        this.excludesA[(count2++)] = st2.nextToken();
    }
  }

  public void setIncludes(String s)
  {
    if (s != null) {
      StringTokenizer st1 = new StringTokenizer(transform(s), ",");
      this.includesA = new String[st1.countTokens()];
      int count1 = 0;
      while (st1.hasMoreTokens())
        this.includesA[(count1++)] = st1.nextToken();
    }
  }

  public String getCharSet()
  {
    return this.charSet;
  }

  public void setCharSet(String string)
  {
    this.charSet = string;
  }

  public void afterPropertiesSet()
    throws Exception
  {
  }

  public void setApplicationContext(ApplicationContext applicationContext)
    throws BeansException
  {
    this.applicationContext = applicationContext;
  }

  public ApplicationContext getApplicationContext()
  {
    return this.applicationContext;
  }

  public boolean contains(String enumName) {
    return this.enumsHash.containsKey(enumName);
  }
}
package org.webframework.mvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.BeanFactory;
import org.webframework.Command;
import org.webframework.exception.WebMvcException;

public class WebCommandFactory
{
  private static Log log = LogFactory.getLog(WebCommandFactory.class);
  private static WebCommandFactory instance = new WebCommandFactory();

  public static WebCommandFactory getInstance() { return instance;
  }

  protected WebMvcConfig getMvcConfig()
  {
    return (WebMvcConfig)BeanFactory.getBean("webMvcConfig");
  }

  protected String getDefaultPackagePrefix()
  {
    String prefix = getMvcConfig().getDefaultPackagePrefix();
    return prefix + '.';
  }

  protected String[] getExludePackagePrefixArray()
  {
    String excludePrefix = getMvcConfig().getExcludePackagePrefix();
    return excludePrefix == null ? new String[0] : excludePrefix.split(",");
  }

  protected String getCommandClassPath(String commandId)
  {
    String defaultPackagePrefix = getDefaultPackagePrefix();
    String[] exludePackagePrefix = getExludePackagePrefixArray();
    boolean needPrefix = true;
    for (int i = 0; i < exludePackagePrefix.length; i++) {
      if (commandId.startsWith(exludePackagePrefix[i])) {
        needPrefix = false;
        break;
      }
    }
    if (needPrefix) return defaultPackagePrefix + commandId;
    return commandId;
  }

  public Command getCommand(String commandId)
    throws WebMvcException
  {
    String commandClassPath = getCommandClassPath(commandId);
    try {
      return (Command)Class.forName(commandClassPath).newInstance();
    } catch (InstantiationException e) {
      log.error("实例化Command类时产生异常", e);
      throw new WebMvcException("调用" + commandId + "时产生异常，无法调用构造函数", e);
    } catch (IllegalAccessException e) {
      log.error("实例化Command类时产生异常", e);
      throw new WebMvcException("调用" + commandId + "时产生异常，没有相关类的访问权限", e);
    } catch (ClassNotFoundException e) {
      log.error("实例化Command类时产生异常", e);
      throw new WebMvcException("调用" + commandId + "时产生异常，找不到相关类", e);
    } catch (ClassCastException e) {
      log.error("实例化Command类时产生异常", e);
      throw new WebMvcException("调用" + commandId + "时产生异常，相关类必须继承自" + Command.class.getName(), e);
    }
  }
}
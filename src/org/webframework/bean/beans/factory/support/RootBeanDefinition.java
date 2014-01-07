package org.webframework.bean.beans.factory.support;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.webframework.bean.beans.MutablePropertyValues;
import org.webframework.bean.beans.PropertyValues;

public class RootBeanDefinition extends AbstractBeanDefinition
{
  public static final int DEPENDENCY_CHECK_NONE = 0;
  public static final int DEPENDENCY_CHECK_OBJECTS = 1;
  public static final int DEPENDENCY_CHECK_SIMPLE = 2;
  public static final int DEPENDENCY_CHECK_ALL = 3;
  public static final int AUTOWIRE_NO = 10;
  public static final int AUTOWIRE_BY_NAME = 11;
  public static final int AUTOWIRE_BY_TYPE = 12;
  public static final int AUTOWIRE_CONSTRUCTOR = 13;
  private Class beanClass;
  private ConstructorArgumentValues constructorArgumentValues;
  private String[] dependsOn;
  private int dependencyCheck = 0;

  private int autowire = 10;
  private String initMethodName;
  private String destroyMethodName;

  public RootBeanDefinition(Class beanClass, PropertyValues pvs)
  {
    super(pvs);
    this.beanClass = beanClass;
  }

  public RootBeanDefinition(Class beanClass, PropertyValues pvs, boolean singleton)
  {
    super(pvs);
    this.beanClass = beanClass;
    setSingleton(singleton);
  }

  public RootBeanDefinition(Class beanClass, ConstructorArgumentValues cargs, PropertyValues pvs)
  {
    super(pvs);
    this.beanClass = beanClass;
    this.constructorArgumentValues = cargs;
  }

  public RootBeanDefinition(Class beanClass, ConstructorArgumentValues cargs, PropertyValues pvs, boolean singleton)
  {
    super(pvs);
    this.beanClass = beanClass;
    this.constructorArgumentValues = cargs;
    setSingleton(singleton);
  }

  public RootBeanDefinition(Class beanClass, PropertyValues pvs, boolean singleton, int dependencyCheck, int autowire)
  {
    super(pvs);
    this.beanClass = beanClass;
    setSingleton(singleton);
    setDependencyCheck(dependencyCheck);
    setAutowire(autowire);
  }

  public RootBeanDefinition(RootBeanDefinition other)
  {
    super(new MutablePropertyValues(other.getPropertyValues()));
    this.beanClass = other.beanClass;
    this.constructorArgumentValues = other.constructorArgumentValues;
    setSingleton(other.isSingleton());
    setLazyInit(other.isLazyInit());
    setDependsOn(other.getDependsOn());
    setDependencyCheck(other.getDependencyCheck());
    setAutowire(other.getAutowire());
    setInitMethodName(other.getInitMethodName());
    setDestroyMethodName(other.getDestroyMethodName());
  }

  public final Class getBeanClass()
  {
    return this.beanClass;
  }

  public ConstructorArgumentValues getConstructorArgumentValues()
  {
    return this.constructorArgumentValues;
  }

  public boolean hasConstructorArgumentValues()
  {
    return (this.constructorArgumentValues != null) && (!this.constructorArgumentValues.isEmpty());
  }

  public void setDependsOn(String[] dependsOn)
  {
    this.dependsOn = dependsOn;
  }

  public String[] getDependsOn()
  {
    return this.dependsOn;
  }

  public void setDependencyCheck(int dependencyCheck)
  {
    this.dependencyCheck = dependencyCheck;
  }

  public int getDependencyCheck()
  {
    return this.dependencyCheck;
  }

  public void setAutowire(int autowire)
  {
    this.autowire = autowire;
  }

  public int getAutowire()
  {
    return this.autowire;
  }

  public void setInitMethodName(String initMethodName)
  {
    this.initMethodName = initMethodName;
  }

  public String getInitMethodName()
  {
    return this.initMethodName;
  }

  public void setDestroyMethodName(String destroyMethodName)
  {
    this.destroyMethodName = destroyMethodName;
  }

  public String getDestroyMethodName()
  {
    return this.destroyMethodName;
  }

  public void validate() throws BeanDefinitionValidationException
  {
    super.validate();
    if (this.beanClass == null) {
      throw new BeanDefinitionValidationException("beanClass must be set in RootBeanDefinition");
    }
    if ((FactoryBean.class.isAssignableFrom(this.beanClass)) && (!isSingleton()))
      throw new BeanDefinitionValidationException("FactoryBean must be defined as singleton - FactoryBeans themselves are not allowed to be prototypes");
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof RootBeanDefinition))
      return false;
    return (super.equals(obj)) && (((RootBeanDefinition)obj).getBeanClass().equals(getBeanClass()));
  }

  public String toString() {
    return "Root bean definition with class [" + getBeanClass().getName() + "]";
  }
}
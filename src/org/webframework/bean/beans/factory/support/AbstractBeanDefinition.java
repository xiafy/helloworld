package org.webframework.bean.beans.factory.support;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.webframework.bean.beans.MutablePropertyValues;
import org.webframework.bean.beans.PropertyValues;

public abstract class AbstractBeanDefinition
{
  private PropertyValues propertyValues;
  private boolean singleton = true;

  private boolean lazyInit = false;

  protected AbstractBeanDefinition(PropertyValues pvs)
  {
    this.propertyValues = (pvs != null ? pvs : new MutablePropertyValues());
  }

  public PropertyValues getPropertyValues()
  {
    return this.propertyValues;
  }

  public void addPropertyValue(PropertyValue pv)
  {
    if (!(this.propertyValues instanceof MutablePropertyValues))
    {
      this.propertyValues = new MutablePropertyValues(getPropertyValues());
    }
    ((MutablePropertyValues)this.propertyValues).addPropertyValue(pv);
  }

  public void setSingleton(boolean singleton)
  {
    this.singleton = singleton;
  }

  public boolean isSingleton()
  {
    return this.singleton;
  }

  public void setLazyInit(boolean lazyInit)
  {
    this.lazyInit = lazyInit;
  }

  public boolean isLazyInit()
  {
    return this.lazyInit;
  }

  public void validate() throws BeanDefinitionValidationException {
    if ((this.lazyInit) && (!this.singleton))
      throw new BeanDefinitionValidationException("Lazy initialization is just applicable for singleton beans");
  }

  public boolean equals(Object other)
  {
    if (!(other instanceof AbstractBeanDefinition))
      return false;
    AbstractBeanDefinition obd = (AbstractBeanDefinition)other;
    return this.propertyValues.changesSince(obd.propertyValues).getPropertyValues().length == 0;
  }
}
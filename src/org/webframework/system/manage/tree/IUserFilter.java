package org.webframework.system.manage.tree;

import java.util.List;
import org.webframework.WMap;

public abstract interface IUserFilter
{
  public abstract List filterUser(WMap paramWMap);
}
package org.webframework.system.index;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.webframework.BeanFactory;
import org.webframework.Command;
import org.webframework.WMap;
import org.webframework.mvc.AjaxView;
import org.webframework.mvc.View;
import org.webframework.system.SysConfig;
import org.webframework.system.SysContext;
import org.webframework.system.login.User;
import org.webframework.system.login.bean.SystemConfig;
import org.webframework.system.login.bean.UserConfig;

public class Index extends Command
{
  IndexDao indexDao = new IndexDao();

  public String index(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    User user = SysContext.getSysUser();
    if (user != null)
    {
      SysContext.refreshUserConfig(request.getSession());

      String avatarId = SysContext.getUserConfigBean(request.getSession()).getUserAvatar();
      if ((avatarId == null) || ("".equals(avatarId))) {
        avatarId = SysContext.getSystemConfigBean().getUserAvatar();
      }
      if ((avatarId != null) && (!"".equals(avatarId))) {
        request.setAttribute("_Web_key_user_avatar", this.indexDao.getUserAvatar(avatarId));
      }
    }

    return ((SysConfig)BeanFactory.getBean("sysConfig")).getIndexPage();
  }

  public void queryLastLoginLog(HttpServletRequest request, HttpServletResponse response, AjaxView view)
    throws Exception
  {
    WMap map = this.indexDao.getUserLastLoginLog(SysContext.getSysUser().getCzryDm());
    if (map != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String time = map.getString("time");
      long last = sdf.parse(time).getTime();
      long today = sdf.parse(sdf.format(new Date())).getTime();
      long diffDays = (today - last) / 86400000L;
      if (diffDays <= 2L) {
        time = time.substring("yyyy-MM-dd".length());
        time = time.substring(0, "HH:MI".length() + 1);
        if (diffDays == 0L)
          map.put("time", "今天" + time);
        else if (diffDays == 1L)
          map.put("time", "昨天" + time);
        else if (diffDays == 2L) {
          map.put("time", "前天" + time);
        }
      }
    }
    view.set("log", map);
  }
}
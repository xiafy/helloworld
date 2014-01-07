package org.webframework.system.plugin.cp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.webframework.BeanFactory;
import org.webframework.Command;
import org.webframework.WMap;
import org.webframework.mvc.AjaxView;
import org.webframework.mvc.View;
import org.webframework.system.SysCommon;
import org.webframework.system.SysConfig;
import org.webframework.system.SysContext;
import org.webframework.system.login.User;
import org.webframework.system.login.auth.algorithm.IEncrypt;

public class ControlPanel extends Command
{
  public static final String KEY_FONT_SIZE = "style_font_size";
  public static final String KEY_FONT_FAMILY = "style_font_family";
  public static final String KEY_THEME = "style_theme";
  public static final String KEY_AVATAR = "user_avatar";
  ControlPanelDao dao = new ControlPanelDao();

  public String index(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    String czryDm = SysContext.getSysUser().getCzryDm();

    view.putAll(SysCommon.getUserMapById(czryDm));

    WMap userParams = this.dao.getUserParams(czryDm);
    view.putAll(userParams);

    String avatarImgId = null;
    if (userParams.containsKey("user_avatar"))
      avatarImgId = userParams.getString("user_avatar");
    else {
      avatarImgId = this.dao.getDefaultAvatar();
    }
    WMap avatar = this.dao.getAvatar(avatarImgId);
    if (avatar != null) {
      view.set("avatarImgPath", avatar.getString("imgPath"));
    }
    return "jsp/system/plugin/cp/index.jsp";
  }

  public String selectAvatar(HttpServletRequest request, HttpServletResponse response, View view)
    throws Exception
  {
    return "jsp/system/plugin/cp/select_avatar.jsp";
  }

  public void getAvatars(HttpServletRequest request, HttpServletResponse response, AjaxView view)
    throws Exception
  {
    view.set("avatars", this.dao.getAvatars());
  }

  public void setFont(HttpServletRequest request, HttpServletResponse response, AjaxView view)
    throws Exception
  {
    String czryDm = SysContext.getSysUser().getCzryDm();
    String fontSize = view.getString("fontSize");
    String fontFamily = view.getString("fontFamily");
    this.dao.resetUserFont(request, czryDm, fontSize, fontFamily);
  }

  public void setTheme(HttpServletRequest request, HttpServletResponse response, AjaxView view)
    throws Exception
  {
    String czryDm = SysContext.getSysUser().getCzryDm();
    String theme = view.getString("theme");
    this.dao.resetUserTheme(request, czryDm, theme);
  }

  public void setAvatar(HttpServletRequest request, HttpServletResponse response, AjaxView view)
    throws Exception
  {
    String czryDm = SysContext.getSysUser().getCzryDm();
    String avatarId = view.getString("avatarId");
    this.dao.resetUserAvatar(request, czryDm, avatarId);
  }

  public void getThemes(HttpServletRequest request, HttpServletResponse response, AjaxView view)
    throws Exception
  {
    view.set("themes", this.dao.getThemes());
  }

  public void saveAll(HttpServletRequest request, HttpServletResponse response, AjaxView view) throws Exception
  {
    String czryDm = SysContext.getSysUser().getCzryDm();
    String password = view.getString("password");
    if ((password != null) && (!"".equals(password))) {
      IEncrypt passwordEncrypt = ((SysConfig)BeanFactory.getBean("sysConfig")).getPasswordEncrypt();
      if (!passwordEncrypt.encode(password).equals(this.dao.getPassword(czryDm))) {
        view.setErrorMsg("原密码不正确请确认!");
        return;
      }
    }
    this.dao.saveAll(czryDm, view);
  }
}
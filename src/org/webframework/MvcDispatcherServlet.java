package org.webframework;

import inspur.tax.log.LogUtil;
import java.beans.Introspector;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webframework.mvc.View;
import org.webframework.mvc.WebCommandProcessor;
import org.webframework.mvc.WebMvcConfig;

public class MvcDispatcherServlet extends HttpServlet
{
  protected static Log log = LogFactory.getLog(MvcDispatcherServlet.class);

  public void init() throws ServletException {
    super.init();
    Introspector.flushCaches();
  }

  public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    try
    {
      WebCommandProcessor processor = new WebCommandProcessor();

      Object view = processor.processRequest(getServletContext(), request, response);
      if ((view instanceof View))
      {
        String nextJspPage = ((View)view).getNextJspPath();
        if (nextJspPage != null)
          if (((View)view).isRedirect()) {
            response.sendRedirect(nextJspPage);
          } else {
            request.setAttribute("_view", view);
            request.setAttribute("_jsp_path", nextJspPage);
            RequestDispatcher dispatcher = request.getRequestDispatcher(nextJspPage);
            dispatcher.forward(request, response);
          }
      }
      else if ((view instanceof String))
      {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write((String)view);
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.error("执行请求时产生了异常:" + e.getMessage(), e);
      try {
        processException(request, response, e);
      } catch (Exception ex) {
        processError(request, response, e, ex);
      }
    }
  }

  private void processException(HttpServletRequest request, HttpServletResponse response, Exception e)
  {
    Throwable t = e;
    try {
      if ((e instanceof InvocationTargetException)) t = e.getCause();

      LogUtil.addLogYwcz(request, e, "", "00", "0");
      WebMvcConfig config = (WebMvcConfig)BeanFactory.getBean("webMvcConfig");
      String errorPage = config.getErrorPage();
      request.setAttribute("_jsp_path", errorPage);
      request.setAttribute("_exception", t);
      request.getRequestDispatcher(errorPage).forward(request, response);
    } catch (Exception t1) {
      processError(request, response, t, t1);
    }
  }

  private void processError(HttpServletRequest request, HttpServletResponse response, Throwable e, Exception t)
  {
    try
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

      response.setContentType("text/html;charset=GBK");
      response.setStatus(500);
      PrintWriter writer = response.getWriter();

      writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
      writer.write("<HTML><HEAD>");
      writer.write("<META content=\"text/html; charset=gbk\" http-equiv=Content-Type>");
      writer.write("<style type=\"text/css\">");
      writer.write("body {background-color: #F1F9FA;}");
      writer.write("#mainTitle {color: #303778;font-family: \"微软雅黑\", \"幼圆\", \"宋体\", \"楷体\" ;font-weight:normal;font-size:20px;margin-left:20px;margin-top:30px;}");
      writer.write("hr {width:70%;margin-left:20px;margin-top:-15px;}");
      writer.write(".title {margin-left:20px;font-size:14px;color: #333333;font-family:\"宋体\",\"微软雅黑\", \"幼圆\",  \"楷体\"; padding-top:5px;}");
      writer.write(".title b {color:#3077AB;}");
      writer.write(".text {font-family: \"宋体\",\"Arial Unicode MS\", \"仿宋\", \"黑体\";font-size:12px;margin-left:20px;}");
      writer.write("#subhead {color:#303778;font-family: \"微软雅黑\", \"幼圆\", \"宋体\", \"楷体\";font-weight:normal;font-size:18px;margin-left:20px;margin-top:30px;}");
      writer.write("</style></HEAD>");
      writer.write("<BODY>");
      writer.write("<H2 id=\"mainTitle\">执行请求时产生了异常</H2>");
      writer.write("<hr align=\"left\" size=\"1\" noshade>");
      writer.write("<DIV class=\"title\"><B>异常信息：</B>");
      writer.write(e.getMessage() == null ? "无特殊信息" : e.getMessage());
      writer.write("</DIV>");
      writer.write("<DIV class=\"title\"><B>异常类：</B>");
      writer.write(e.getClass().getName());
      writer.write("</DIV>");
      writer.write("<DIV class=\"title\"><B>服务器时间：</B>");
      writer.write(sdf.format(Calendar.getInstance().getTime()));
      writer.write("</DIV>");
      writer.write("<div class=\"text\"><PRE>");
      e.printStackTrace(writer);
      writer.write("</PRE></div>");
      writer.write("<H3 id=\"subhead\">同时由于如下异常，无法正常显示友好的错误页面</H3>");
      writer.write("<div class=\"text\"><PRE>");
      t.printStackTrace(writer);
      writer.write("</PRE></div></BODY></HTML>");
    }
    catch (Exception ignored) {
      log.error("程序产生了无法输出到页面上的异常", ignored);
    }
  }
}
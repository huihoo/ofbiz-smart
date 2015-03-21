package org.huihoo.ofbiz.smart.webapp.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 界面处理接口
 * <p>
 * 
 * @author huangbohua
 *
 */
public interface ViewHandler {
  /**
   * 渲染响应界面
   * 
   * @param page 要渲染的界面
   * @param layout 界面应用的布局
   * @param req HttpServletRequesty 请求对象
   * @param resp HttpServletResponse 响应对象
   * @throws ViewHandlerException
   */
  public void render(String page, String layout, HttpServletRequest req, HttpServletResponse resp) throws ViewHandlerException;
}

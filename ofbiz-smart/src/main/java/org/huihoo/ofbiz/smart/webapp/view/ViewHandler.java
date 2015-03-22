/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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

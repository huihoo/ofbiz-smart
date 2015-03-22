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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;

public class JspViewHandler implements ViewHandler {

  @Override
  public void render(String page, String layout, HttpServletRequest req, HttpServletResponse resp)
          throws ViewHandlerException {
    String jspViewBasePath = (String) req.getAttribute("jspViewBasePath");
    if(jspViewBasePath == null)
      jspViewBasePath = "/WEB-INF/jsp";
    RequestDispatcher dispatcher = null;
    if (C.RESP_NONE.equals(layout)) {
      dispatcher = req.getRequestDispatcher(jspViewBasePath+page);
    }else{
      dispatcher = req.getRequestDispatcher(jspViewBasePath+(layout == null ? "/layout.jsp" : layout));
      req.setAttribute("pageContent", jspViewBasePath+page);
    }

    try {
      dispatcher.forward(req, resp);
    } catch (ServletException e) {
      throw new ViewHandlerException(e);
    } catch (IOException e) {
      throw new ViewHandlerException(e);
    }

  }

}

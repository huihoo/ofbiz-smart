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
package org.huihoo.ofbiz.smart.webapp.taglib;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;




/**
 * <p>
 * 页面分页标签处理类 会生成如下形式分页<br/>
 * 
 * <code> < 1 2 3 ... 4 5 > </code> <br/>
 * <code> < 1 2 3 4 5 ...10 11... 90 91 > </code>
 * <p>
 * <br/>
 * 对应的HTML代码为: <br/>
 * 
 * <pre>
 * {@code<ul class="">}<br/>
 * {@code   <li><a href="#"><i class=""></i></a></li>}<br/>
 * {@code   <li><a href="#">1</a></li>}<br/>
 * {@code   <li><a href="#">2</a></li>}<br/>
 * {@code   <li class=''><a href="#">3</a></li>}<br/>
 * {@code   <li><a href="#">...</a></li>}<br/>
 * {@code   <li><a href="#">4</a></li>}<br/>
 * {@code   <li><a href="#">5</a></li>}<br/>
 * {@code   <li><a href="#"><i class=""></i></a></li>}<br/>
 * {@code </ul>"}
 * </pre>
 * 
 * <p>
 * 其中:<br/>
 * ul中的class为自定义的分页样式class<br/>
 * i中的class为上一页或下一页图标的样式class<br/>
 * li中的class为当前选中页的样式class或不可点的样式class<br/>
 * 
 * </p>
 * 
 * @author huangbohua
 * @since 1.0
 *
 */
public class PaginationTag extends TagSupport {
  private static final long serialVersionUID = 1L;
  private static final String TAG = PaginationTag.class.getName();
  private String lineSepa = System.getProperty("line.separator");
  /**
   * 显示总的页数
   */
  protected int displayNum = 10;
  /**
   * 分页中间分割符号后显示的页数
   */
  protected int endDisplayNum = 2;
  /**
   * 总记录数
   */
  protected int totalEntry = 0;
  /**
   * 总页面数
   */
  protected int totalPage = 0;
  /**
   * 当前第几页
   */
  protected int currentPage = 1;
  /**
   * 分页参数名标识
   */
  protected String pageFlag = "pageNo";
  /**
   * 分页连接
   */
  protected String pageLink = "";
  /**
   * 上一页图标样式
   */
  protected String prevIconClass = "icon-chevron-left";
  /**
   * 下一页图标样式
   */
  protected String nextIconClass = "icon-chevron-right";
  /**
   * 分页容器的样式Class
   */
  protected String pageContainerClass = "pagination";
  /**
   * 分页选中样式Class
   */
  protected String pageSelectedClass = "active";

  /**
   * 分页不可点击的样式
   */
  protected String disabledClass = "disabled";
  
  /**
   * 上一页字符串
   */
  protected String prevString;
  
  /**
   * 下一页字符串
   */
  protected String nextString;

  /**
   * 分页中间分割符号
   */
  protected String dotFlag = "...";

  @Override
  public int doStartTag() throws JspException {
    try {
      this.currentPage = 1;
      HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
      String cPage = req.getParameter(this.pageFlag);
      if (CommUtil.isNotEmpty(cPage)) {
        if (CommUtil.isNumber(cPage)) this.currentPage = Integer.valueOf(cPage);
      }
      String pageBody = renderPaginationBody();
      Log.d("Pagination : " + pageBody, TAG);
      pageContext.getOut().println(pageBody);
    } catch (IOException e) {
      Log.e(e, "Pagination render has an error.", TAG);
    }
    return EVAL_BODY_INCLUDE;
  }



  private String renderPaginationBody() {
    if (totalPage <= 1) return "";
    int[] interval = getInterval();
    StringBuffer sb = new StringBuffer("<ul class='" + pageContainerClass + "'>");
    // 上一页
    if (this.currentPage > 1) {
      appendItem(sb, this.currentPage - 1, true, false);
    } else {
      sb.append("<li class='" + disabledClass + "'>");
      sb.append(" <a href='#'>");
      if (CommUtil.isNotEmpty(prevString)) {
        sb.append(prevString);
      } else {
        sb.append("   <i class='" + prevIconClass + "'></i>");
      }
      sb.append(" </a>");
      sb.append("</li>");
      sb.append(lineSepa);
    }
    // 开始...
    if (interval[0] > 0 && this.endDisplayNum > 0) {
      int end = Math.min(this.endDisplayNum, interval[0]);
      for (int i = 0; i < end; i++) {
        appendItem(sb, (i + 1), false, false);
      }
      if (this.endDisplayNum < interval[0]) {
        sb.append("<li><a href='#'>" + dotFlag + "</a></li>").append(lineSepa);
      }
    }
    // 中间部分
    for (int i = interval[0]; i < interval[1]; i++) {
      appendItem(sb, (i + 1), false, false);
    }
    // 结束...
    if (interval[1] < this.totalPage && this.endDisplayNum > 0) {
      if (this.totalPage - this.endDisplayNum > interval[1]) {
        sb.append("<li><a href='#'>" + dotFlag + "</a></li>").append(lineSepa);
      }
      int begin = Math.max(this.totalPage - this.endDisplayNum, interval[1]);
      for (int i = begin; i < this.totalPage; i++) {
        appendItem(sb, (i + 1), false, false);
      }
    }
    // 下一页
    if (this.currentPage <= this.totalPage - 1) {
      appendItem(sb, this.currentPage + 1, false, true);
    } else {
      sb.append("<li class='" + disabledClass + "'>");
      sb.append(" <a href='#'>");
      if (CommUtil.isNotEmpty(nextString)) {
        sb.append(nextString);
      } else {
        sb.append("   <i class='" + nextIconClass + "'></i>");
      }
      sb.append(" </a>");
      sb.append("</li>");
      sb.append(lineSepa);
    }

    sb.append("</ul>");
    return sb.toString();
  }

  private void appendItem(StringBuffer sb, int pageNo, boolean isPrevious, boolean isNext) {
    HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
    pageNo = pageNo < 0 ? 0 : (pageNo <= this.totalPage ? pageNo : this.totalPage);

    StringBuilder tmpLink = new StringBuilder();
    if (this.pageLink.indexOf("?") == -1){
      tmpLink.append(this.pageLink).append("?").append(this.pageFlag).append("=").append(pageNo);
    }
    else
      tmpLink.append(this.pageLink).append("&").append(this.pageFlag).append("=").append(pageNo);
    
    Enumeration<String> pNames = req.getParameterNames();
    while(pNames.hasMoreElements()){
      String pName = pNames.nextElement();
      if(pName.equals(pageFlag)) continue;
      String pValue = req.getParameter(pName);
      tmpLink.append("&").append(pName).append("=").append(pValue);
    }

    if (isNext) {
      sb.append("<li>");
      sb.append(" <a href='" + tmpLink + "'>");
      if (CommUtil.isNotEmpty(nextString)) {
        sb.append(nextString);
      } else {
        sb.append("   <i class='" + nextIconClass + "'></i>");
      }
      sb.append(" </a>");
      sb.append("</li>");
      sb.append(lineSepa);
    } else if (isPrevious) {
      sb.append("<li>");
      sb.append(" <a href='" + tmpLink + "'>");
      if (CommUtil.isNotEmpty(prevString)) {
        sb.append(prevString);
      } else {
        sb.append("   <i class='" + prevIconClass + "'></i>");
      }
      sb.append(" </a>");
      sb.append("</li>");
      sb.append(lineSepa);
    } else {
      if (pageNo == this.currentPage) {
        sb.append("<li class='" + pageSelectedClass + "'>");
        sb.append(" <a href='#'>" + pageNo + "</a>");
        sb.append("</li>");
        sb.append(lineSepa);
      } else {
        sb.append("<li><a href='" + tmpLink + "'>" + pageNo + "</a></li>");
        sb.append(lineSepa);
      }
    }
  }

  /**
   * <p>
   * 获取分页中间部分
   * </p>
   * 
   * @return
   */
  public int[] getInterval() {
    int[] interval = new int[2];
    int neHalf = (int) Math.ceil(this.displayNum / 2);
    int upperLimit = this.totalPage - this.displayNum;
    int start =
            this.currentPage > neHalf ? Math
                    .max(Math.min(this.currentPage - neHalf, upperLimit), 0) : 0;
    int end =
            this.currentPage > neHalf ? Math.min(this.currentPage + neHalf, this.totalPage) : Math
                    .min(this.displayNum, this.totalPage);
    interval[0] = start;
    interval[1] = end;
    return interval;
  }



  public int getDisplayNum() {
    return displayNum;
  }



  public void setDisplayNum(int displayNum) {
    this.displayNum = displayNum;
  }



  public int getEndDisplayNum() {
    return endDisplayNum;
  }



  public void setEndDisplayNum(int endDisplayNum) {
    this.endDisplayNum = endDisplayNum;
  }



  public int getTotalEntry() {
    return totalEntry;
  }



  public void setTotalEntry(int totalEntry) {
    this.totalEntry = totalEntry;
  }



  public int getTotalPage() {
    return totalPage;
  }



  public void setTotalPage(int totalPage) {
    this.totalPage = totalPage;
  }



  public int getCurrentPage() {
    return currentPage;
  }



  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }



  public String getPageFlag() {
    return pageFlag;
  }



  public void setPageFlag(String pageFlag) {
    this.pageFlag = pageFlag;
  }



  public String getPageLink() {
    return pageLink;
  }



  public void setPageLink(String pageLink) {
    this.pageLink = pageLink;
  }



  public String getPrevIconClass() {
    return prevIconClass;
  }



  public void setPrevIconClass(String prevIconClass) {
    this.prevIconClass = prevIconClass;
  }



  public String getNextIconClass() {
    return nextIconClass;
  }



  public void setNextIconClass(String nextIconClass) {
    this.nextIconClass = nextIconClass;
  }



  public String getPageContainerClass() {
    return pageContainerClass;
  }



  public void setPageContainerClass(String pageContainerClass) {
    this.pageContainerClass = pageContainerClass;
  }



  public String getPageSelectedClass() {
    return pageSelectedClass;
  }



  public void setPageSelectedClass(String pageSelectedClass) {
    this.pageSelectedClass = pageSelectedClass;
  }



  public String getDisabledClass() {
    return disabledClass;
  }



  public void setDisabledClass(String disabledClass) {
    this.disabledClass = disabledClass;
  }



  public String getDotFlag() {
    return dotFlag;
  }



  public void setDotFlag(String dotFlag) {
    this.dotFlag = dotFlag;
  }



  public String getPrevString() {
    return prevString;
  }



  public String getNextString() {
    return nextString;
  }



  public void setPrevString(String prevString) {
    this.prevString = prevString;
  }



  public void setNextString(String nextString) {
    this.nextString = nextString;
  }
}

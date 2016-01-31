package org.huihoo.ofbiz.smart.webapp.taglib;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.huihoo.ofbiz.smart.base.util.I18NUtil;
import org.huihoo.ofbiz.smart.base.util.Log;

public class I18nTag extends TagSupport {
  private static final long serialVersionUID = 1L;
  private final static String TAG = I18nTag.class.getName();
  
  private String resourceName;
  private String key;

  @Override
  public int doStartTag() throws JspException {
    try {
      HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
      ResourceBundle rb = I18NUtil.getResourceBundle(resourceName, req.getLocale());
      if (rb != null) {
        String val = rb.getString(key);
        pageContext.getOut().println(val);
      } else {
        pageContext.getOut().println("");
      }
    } catch (IOException e) {
      Log.e(e, "I18nTag render has an error.", TAG);
    }
    return EVAL_BODY_INCLUDE;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }
}

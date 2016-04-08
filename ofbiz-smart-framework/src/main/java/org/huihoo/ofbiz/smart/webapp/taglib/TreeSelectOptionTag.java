package org.huihoo.ofbiz.smart.webapp.taglib;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;

import ognl.Ognl;
import ognl.OgnlException;

public class TreeSelectOptionTag extends TagSupport {
  private static final long serialVersionUID = 1L;
  private static final String TAG = TreeSelectOptionTag.class.getName();
  private String lineSepa = System.getProperty("line.separator");
  private String className;
  private Object currentValue;
  private String condition;
  private String labelName = "name";
  private String valueName = "id";
  private String orderBy;
  private int liveTimeInSeconds;

  @Override
  public int doStartTag() throws JspException {
    try {
      Class<?> clazz = Class.forName(className);
      Delegator delegator = (Delegator) pageContext.getServletContext().getAttribute(C.CTX_DELEGATOR);
      String defaultCondition = "{parent.id,isNull,any}";
      if (CommUtil.isNotEmpty(condition)) {
        defaultCondition = defaultCondition + condition;
      }
      List<?> allObjects = (List<?>) delegator.findListByCond(clazz, defaultCondition);

      StringBuilder treeSb = new StringBuilder();
      for (Object obj : allObjects) {
        String select = "";
        Object id = Ognl.getValue(valueName, obj);
        if (id != null && String.valueOf(id).equals(String.valueOf(currentValue))) {
          select = "selected='selected'";
        }
        treeSb.append("<option value='" + id + "' " + select + ">");
        treeSb.append(Ognl.getValue(labelName, obj));
        treeSb.append("</option>");
        treeSb.append(lineSepa);
        recursion(obj, delegator, treeSb,currentValue);
      }

      pageContext.getOut().println(treeSb.toString());
    } catch (Exception e) {
      Log.e(e, e.getMessage(), TAG);
    }
    return EVAL_BODY_INCLUDE;
  }
  
  private int countObjectDepth(Object c) throws OgnlException {
    int depth = 1;
    if (c == null) {
      return depth;
    }
    Object parent = Ognl.getValue("parent", c);
    if (parent != null) {
      depth++;
      countObjectDepth(parent);
    }
    return depth;
  }

  private void recursion(Object c, Delegator delegator, StringBuilder sb, Object currentId)
      throws GenericEntityException, OgnlException {
    List<?> children =
        (List<?>) delegator.findListByAnd(c.getClass(), CommUtil.toMap("parent.id", Ognl.getValue("id", c)));
   
    if (CommUtil.isNotEmpty(children)) {
      int depth = countObjectDepth(c);
      for (Object obj : children) {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < depth; i++) {
          space.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        String select = "";
        Object id = Ognl.getValue(valueName, obj);
        String idstr = String.valueOf(id);
        
        if (currentValue != null && currentValue instanceof Collection) {
          Collection<?> collection = (Collection<?>) currentValue;
          for (Object object : collection) {
            Object cid = Ognl.getValue("id", object);
            if (idstr.equals(String.valueOf(cid))) {
              select = "selected='selected'";
              break;
            }
          }
        } else {
          String cstr = String.valueOf(currentValue);
          if (id != null && idstr.equals(cstr)) {
            select = "selected='selected'";
          } else {
            if (CommUtil.isNotEmpty(cstr) && cstr.indexOf(",") >= 0 && Arrays.asList(cstr.split(",")).contains(idstr)) {
              select = "selected='selected'";
            }
          }
        }
        
        sb.append("<option value='" + id + "' " + select + ">");
        sb.append(space.toString() + Ognl.getValue(labelName, obj));
        sb.append("</option>").append(lineSepa);
        recursion(obj, delegator, sb, currentId);
      }
    }
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }



  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getLineSepa() {
    return lineSepa;
  }

  public void setLineSepa(String lineSepa) {
    this.lineSepa = lineSepa;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public int getLiveTimeInSeconds() {
    return liveTimeInSeconds;
  }

  public void setLiveTimeInSeconds(int liveTimeInSeconds) {
    this.liveTimeInSeconds = liveTimeInSeconds;
  }

  public Object getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(Object currentValue) {
    this.currentValue = currentValue;
  }

  public String getLabelName() {
    return labelName;
  }

  public String getValueName() {
    return valueName;
  }

  public void setLabelName(String labelName) {
    this.labelName = labelName;
  }

  public void setValueName(String valueName) {
    this.valueName = valueName;
  }
}

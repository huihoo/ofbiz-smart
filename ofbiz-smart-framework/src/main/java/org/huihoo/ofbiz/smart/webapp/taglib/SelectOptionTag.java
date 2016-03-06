package org.huihoo.ofbiz.smart.webapp.taglib;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;

import ognl.Ognl;
import ognl.OgnlException;


public class SelectOptionTag extends TagSupport {
  private static final long serialVersionUID = 1L;
  private static final String TAG = SelectOptionTag.class.getName();
  private String lineSepa = System.getProperty("line.separator");

  private String className;
  private String labelName = "name";
  private String valueName = "id";
  private Object currentValue;
  private String condition;
  private String orderBy;
  private boolean useCache;
  private int liveTimeInSeconds;

  @SuppressWarnings("rawtypes")
  @Override
  public int doStartTag() throws JspException {
    try {
      Class<?> clazz = Class.forName(className);
      Delegator delegator = (Delegator) pageContext.getServletContext().getAttribute(C.CTX_DELEGATOR);
      
      Set<String> fieldsToSelect = new LinkedHashSet<>();
      List<String> orderByList = Arrays.asList(new String[]{});
      if (CommUtil.isNotEmpty(orderBy)) {
        String[] orderByToken = orderBy.split(",");
        orderByList = Arrays.asList(orderByToken);
      }
      
      List<?> allObjects = (List<?>) delegator.findListByCond(clazz,condition,fieldsToSelect,orderByList,useCache,liveTimeInSeconds);
      
      StringBuilder optionSb = new StringBuilder();
      for (Object obj : allObjects) {
        String select = "";
        Object id = Ognl.getValue(valueName, obj);
        String idstr = String.valueOf(id);
        
        if (currentValue != null && currentValue instanceof Collection) {
          Collection collection = (Collection) currentValue;
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
        
        optionSb.append("<option value='"+id+"' "+select+">"+Ognl.getValue(labelName, obj)+"</option>").append(lineSepa);
      }
      Log.d("Select :" + optionSb, TAG);
      pageContext.getOut().println(optionSb.toString());
    } catch (IOException e) {
      Log.e(e, e.getMessage(), TAG);
    } catch (ClassNotFoundException e) {
      Log.e(e, e.getMessage(), TAG);
    } catch (GenericEntityException e) {
      Log.e(e, e.getMessage(), TAG);
    } catch (OgnlException e) {
      Log.e(e, e.getMessage(), TAG);
    }
    return EVAL_BODY_INCLUDE;
  }
  
  
  

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }



  public String getLabelName() {
    return labelName;
  }




  public void setLabelName(String labelName) {
    this.labelName = labelName;
  }




  public String getValueName() {
    return valueName;
  }




  public void setValueName(String valueName) {
    this.valueName = valueName;
  }




  public String getCondition() {
    return condition;
  }




  public void setCondition(String condition) {
    this.condition = condition;
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




  public boolean isUseCache() {
    return useCache;
  }




  public void setUseCache(boolean useCache) {
    this.useCache = useCache;
  }




  public String getOrderBy() {
    return orderBy;
  }




  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

}

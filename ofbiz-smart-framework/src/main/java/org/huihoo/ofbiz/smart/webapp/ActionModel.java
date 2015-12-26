package org.huihoo.ofbiz.smart.webapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActionModel implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public List<Action> actionList = new ArrayList<>();
  
  public static class Action implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String uri;

    public String method;

    public String processType;
    
    public boolean requireAuth;
    
    public String pageTitle;
    
    public String moreCss;
    
    public String moreJavascripts;

    public List<ServiceCall> serviceCallList= new ArrayList<>();
    
    public Response response;
    
    public String navTag;

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Action [uri=");
      builder.append(uri);
      builder.append(", method=");
      builder.append(method);
      builder.append(", processType=");
      builder.append(processType);
      builder.append(", requireAuth=");
      builder.append(requireAuth);
      builder.append(", pageTitle=");
      builder.append(pageTitle);
      builder.append(", moreCss=");
      builder.append(moreCss);
      builder.append(", moreJavascripts=");
      builder.append(moreJavascripts);
      builder.append(", serviceCallList=");
      builder.append(serviceCallList);
      builder.append(", response=");
      builder.append(response);
      builder.append("]");
      return builder.toString();
    }
  }
  
  

  public static class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String viewType;
    
    public String viewName;
    
    public String layout;

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Response [viewType=");
      builder.append(viewType);
      builder.append(", viewName=");
      builder.append(viewName);
      builder.append(", layout=");
      builder.append(layout);
      builder.append("]");
      return builder.toString();
    }
    
  }

  public static class ServiceCall implements Serializable {
    private static final long serialVersionUID = 1L;
    public String serviceName;
    public String entityName;
    public String paramPairs; 
    public String condition;
    public String orderBy;
    public int liveTimeInSeconds;
    public String resultName;
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("ServiceCall [serviceName=");
      builder.append(serviceName);
      builder.append(", entityName=");
      builder.append(entityName);
      builder.append(", paramPairs=");
      builder.append(paramPairs);
      builder.append(", condition=");
      builder.append(condition);
      builder.append(", orderBy=");
      builder.append(orderBy);
      builder.append("]");
      return builder.toString();
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ActionModel [actionList=");
    builder.append(actionList);
    builder.append("]");
    return builder.toString();
  }

  
}

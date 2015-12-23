package org.huihoo.ofbiz.smart.webapp;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class ActionModel implements Serializable {
  private static final long serialVersionUID = 1L;

  public String uri;

  public String method;

  public String processType;

  public Set<ServiceCall> serviceCallSet = new LinkedHashSet<>();

  public static class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String viewName;
    
    public String layout;
    
  }

  public static class ServiceCall implements Serializable {
    private static final long serialVersionUID = 1L;
    public String serviceName;
    public String entityName;
    public String paramPairs; 
    public String condition;
    public String orderBy;
  }
}

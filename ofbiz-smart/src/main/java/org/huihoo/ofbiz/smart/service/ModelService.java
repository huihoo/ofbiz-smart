package org.huihoo.ofbiz.smart.service;

import java.io.Serializable;



public class ModelService implements Serializable, Cloneable {
  private static final long serialVersionUID = 1L;

  public String name;
  public String engineName;
  public String location;
  public String invoke;
  public String defaultEntityName;
  public String description;
  public ServiceEventAction sea;
  public boolean persist = true;
  public boolean auth = false;
  public boolean export = false;
  public boolean useTransaction = false;


  @Override
  public Object clone() {
    ModelService o = null;
    try {
      o = (ModelService) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return o;
  }



  public static class ServiceEventAction implements Cloneable {
    public String seaName;
    public String triggerAt;

    @Override
    public String toString() {
      return "ServiceEventAction [seaName=" + seaName + ", triggerAt=" + triggerAt + "]";
    }

    @Override
    public Object clone() {
      ServiceEventAction o = null;
      try {
        o = (ServiceEventAction) super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return o;
    }
  }



  public String getName() {
    return name;
  }



  public void setName(String name) {
    this.name = name;
  }



  public String getEngineName() {
    return engineName;
  }



  public void setEngineName(String engineName) {
    this.engineName = engineName;
  }



  public String getLocation() {
    return location;
  }



  public void setLocation(String location) {
    this.location = location;
  }



  public String getInvoke() {
    return invoke;
  }



  public void setInvoke(String invoke) {
    this.invoke = invoke;
  }



  public String getDefaultEntityName() {
    return defaultEntityName;
  }



  public void setDefaultEntityName(String defaultEntityName) {
    this.defaultEntityName = defaultEntityName;
  }



  public String getDescription() {
    return description;
  }



  public void setDescription(String description) {
    this.description = description;
  }



  public ServiceEventAction getSea() {
    return sea;
  }



  public void setSea(ServiceEventAction sea) {
    this.sea = sea;
  }



  public boolean isPersist() {
    return persist;
  }



  public void setPersist(boolean persist) {
    this.persist = persist;
  }



  public boolean isAuth() {
    return auth;
  }



  public void setAuth(boolean auth) {
    this.auth = auth;
  }



  public boolean isExport() {
    return export;
  }



  public void setExport(boolean export) {
    this.export = export;
  }



  public boolean isUseTransaction() {
    return useTransaction;
  }



  public void setUseTransaction(boolean useTransaction) {
    this.useTransaction = useTransaction;
  }

}

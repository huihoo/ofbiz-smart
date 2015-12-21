package org.huihoo.ofbiz.smart.service;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServiceModel implements Serializable {

  public String name;
  public String location;
  public String invoke;
  public String engineName;
  public String engityName;
  public String description;
  public boolean requireAuth = false;
  public boolean export = false;
  public boolean persist = true;
  public boolean transaction = false;

  public List<Parameter> parameters = new ArrayList<>();

  static class Parameter {
    public String name;
    public String description;
    public boolean required;
    public String defaultValue;
    public String type;
  }
}

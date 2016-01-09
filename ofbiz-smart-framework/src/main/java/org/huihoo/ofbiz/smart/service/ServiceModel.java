package org.huihoo.ofbiz.smart.service;


import java.io.Serializable;
import java.util.Arrays;

import org.huihoo.ofbiz.smart.service.annotation.Parameter;

public class ServiceModel implements Serializable {
  private static final long serialVersionUID = 1L;
  public String name;
  public String location;
  public String invoke;
  public String engineName;
  public String entityName;
  public String description;
  public boolean requireAuth = false;
  public boolean export = false;
  public boolean persist = true;
  public boolean transaction = false;
  public Class<ServiceCallback>[] callback;
  public Parameter[] parameters;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceModel [name=");
    builder.append(name);
    builder.append(", location=");
    builder.append(location);
    builder.append(", invoke=");
    builder.append(invoke);
    builder.append(", engineName=");
    builder.append(engineName);
    builder.append(", entityName=");
    builder.append(entityName);
    builder.append(", description=");
    builder.append(description);
    builder.append(", requireAuth=");
    builder.append(requireAuth);
    builder.append(", export=");
    builder.append(export);
    builder.append(", persist=");
    builder.append(persist);
    builder.append(", transaction=");
    builder.append(transaction);
    builder.append(", callback=");
    builder.append(Arrays.toString(callback));
    builder.append(", parameters=");
    builder.append(Arrays.toString(parameters));
    builder.append("]");
    return builder.toString();
  }


}

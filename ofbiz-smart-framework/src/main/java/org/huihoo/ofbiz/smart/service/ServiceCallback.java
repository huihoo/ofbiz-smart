package org.huihoo.ofbiz.smart.service;


import java.util.Map;

public interface ServiceCallback {

    void receiveEvent(Map<String,Object> ctx,Map<String,Object> result);

    void receiveEvent(Map<String,Object> ctx,Throwable t);
}

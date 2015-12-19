package org.huihoo.ofbiz.smart.service.engine;


import org.huihoo.ofbiz.smart.service.GenericServiceException;

import java.util.Map;

public interface GenericEngine {

    String getName();

    Map<String, Object> runSync(String serviceName, Map<String, Object> ctx) throws GenericServiceException;

    void runAsync(String localName, Map<String, Object> ctx) throws GenericServiceException;
}

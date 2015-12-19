package org.huihoo.ofbiz.smart.service.engine;


import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;

import java.util.Map;

public class AbstractEngine implements GenericEngine{

    protected ServiceDispatcher serviceDispatcher;

    public AbstractEngine(ServiceDispatcher serviceDispatcher) {
        this.serviceDispatcher = serviceDispatcher;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> runSync(String serviceName, Map<String, Object> ctx) throws GenericServiceException {
        return null;
    }

    @Override
    public void runAsync(String serviceName, Map<String, Object> ctx) throws GenericServiceException {

    }
}

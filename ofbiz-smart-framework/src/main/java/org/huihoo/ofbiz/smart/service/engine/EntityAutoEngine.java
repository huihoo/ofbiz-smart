package org.huihoo.ofbiz.smart.service.engine;



import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;

import java.util.Map;

public class EntityAutoEngine extends GenericAsyncEngine {

    public EntityAutoEngine(ServiceDispatcher serviceDispatcher) {
        super(serviceDispatcher);
    }

    @Override
    public Map<String, Object> runSync(String serviceName, Map<String, Object> ctx) throws GenericServiceException {
        return null;
    }

    @Override
    public String getName() {
        return "entityAuto";
    }
}

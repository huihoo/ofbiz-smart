package org.huihoo.ofbiz.smart.entity;

import org.avaje.agentloader.AgentLoader;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class BaseTestCase {

    protected Delegator delegator;
    protected boolean testMode = true;
    @Before
    public void before() {
        init();
        Assert.assertNotNull(delegator);
        if (testMode) delegator.beginTransaction();
    }

    public void init() {
        try {
            if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent","debug=1;packages=org.huihoo.ofbiz.smart.entity.**")) {
                Log.i("avaje-ebeanorm-agent not found in classpath - not dynamically loaded","BaseTest");
            }
            delegator = new EbeanDelegator();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @After
    public void endup() {
        if (testMode && delegator != null) {
            delegator.rollback();
            delegator.endTransaction();
        }
    }
}

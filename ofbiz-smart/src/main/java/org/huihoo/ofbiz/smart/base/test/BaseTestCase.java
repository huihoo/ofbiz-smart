package org.huihoo.ofbiz.smart.base.test;

import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public abstract class BaseTestCase {

  protected Delegator delegator;
  protected ServiceDispatcher dispatcher;
  protected boolean isTestMdoe = true;

  @Before
  public void setup() {
    init();
    Assert.assertNotNull(delegator);
    Assert.assertNotNull(dispatcher);
    dispatcher.setTestMode(isTestMdoe);
    if (isTestMdoe) delegator.beginTransaction();
  }

  public abstract void init();


  @After
  public void endup() {
    if (isTestMdoe && delegator != null) {
      delegator.rollback();
      delegator.endTransaction();
    }
  }

}

package org.huihoo.ofbiz.smart.entity;

import org.junit.Test;



public class DelegatorTest {
  
  @Test
  public void testInItEbeanDelegator() {
    try {
      Delegator delegator = new EbeanDelegator();
    } catch (GenericEntityException e) {
      e.printStackTrace();
    }
  }
}   

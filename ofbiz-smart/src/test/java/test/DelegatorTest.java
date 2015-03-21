package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EbeanDelegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import entity.Country;

public class DelegatorTest {
  private static final String module = DelegatorTest.class.getName();
  Delegator delegator;

  @Before
  public void init() {
    Properties p = new Properties();
    try {
      p.load(getClass().getResourceAsStream("/datasource-test.properties"));
      delegator = new EbeanDelegator("h2", "entity", p);
    } catch (GenericEntityException | IOException e) {
      e.printStackTrace();
    }
  }


  @SuppressWarnings("unchecked")
  @Test
  public void testAllInOne() {
    Assert.assertNotNull(delegator);
    try {
      Debug.logDebug("创建国家代码", module);
      List<Country> countries = new ArrayList<>();
      Country cn = new Country();
      cn.setCode("CN");
      cn.setName("China");
      countries.add(cn);
      
      Country en = new Country();
      en.setCode("EN");
      en.setName("England");
      countries.add(en);
      
      Country usa = new Country();
      usa.setCode("USA");
      usa.setName("America");
      countries.add(usa);
      
      delegator.save(countries);
      
      List<Country> countriesFromDb = (List<Country>) delegator.findList("Country", null);
      Assert.assertNotNull(countriesFromDb);
      Assert.assertEquals(3, countriesFromDb.size());
      Debug.logDebug("Countries -> "+countriesFromDb, module);
      
      Debug.logInfo("testAllInOne Succeed.", module);
    } catch (GenericEntityException e) {
      Debug.logError(e, "testAllInOne Exception", module);
    }
  }

}

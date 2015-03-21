package test;


import java.io.IOException;
import java.util.Properties;

import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EbeanDelegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.junit.Before;


public class BaseTest {

  Delegator delegator;
  ServiceDispatcher dispatcher;
  
  @Before
  public void init() {
    Properties p = new Properties();
    try {
//      p.put("ebean.ddl.generate", "true");
//      p.put("ebean.ddl.run", "true");
//      p.put("ebean.debug.sql", "true");
//      p.put("ebean.debug.lazyload", "false");
//      p.put("datasource.default", "h2");
//      p.put("datasource.h2.username", "sa");
//      p.put("datasource.h2.password", "");
//      p.put("datasource.h2.databaseUrl", "jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1");
//      p.put("datasource.h2.databaseDriver", "org.h2.Driver");
//      p.put("datasource.h2.minConnections", "1");
//      p.put("datasource.h2.maxConnections", "25");
//      p.put("datasource.h2.heartbeatsql", "select 1");
//      p.put("datasource.h2.isolationlevel", "read_committed");
      
      p.load(getClass().getResourceAsStream("/datasource-test.properties"));

      delegator = new EbeanDelegator("h2", "entity", p);
      dispatcher = new ServiceDispatcher(delegator);
      
    } catch (GenericEntityException | GenericServiceException | IOException e) {
      e.printStackTrace();
    }
  }
}

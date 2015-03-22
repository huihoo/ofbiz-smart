/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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

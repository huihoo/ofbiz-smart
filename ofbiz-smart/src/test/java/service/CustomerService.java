package service;


import java.util.Map;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;


import entity.Address;
import entity.Country;
import entity.Customer;
import entity.CustomerStatus;


public class CustomerService {
  private static final String module = CustomerService.class.getName();

  public static Map<String, Object> createCustomer(Map<String, Object> ctx) {
    Debug.logDebug("Customer Ctx->" + CommUtils.printMap(ctx), module);
    Map<String, Object> success = ServiceUtils.returnSuccess();
    
    String name = (String) ctx.get("name");
    String countryCode = (String) ctx.get("countryCode");
    String city = (String) ctx.get("city");
    String line1 = (String) ctx.get("line1");
    String line2 = (String) ctx.get("line2");
    
    Delegator delegator = (Delegator) ctx.get(C.CTX_DELEGATOR);
    try {
      Country country = new Country();
      country.setCode(countryCode);
      country.setName("Country-Name");
      delegator.save(country);
      
      CustomerStatus cStatus = new CustomerStatus();
      cStatus.setCode("ACTIVE");
      cStatus.setTitle("Active Customer");
      delegator.save(cStatus);
      
      Address address = new Address();
      address.setCountryCode(country);
      address.setCity(city);
      address.setLine1(line1);
      address.setLine2(line2);
      address.setRegion("Region");
      delegator.save(address);
      
      
      Customer customer = new Customer();
      customer.setBillingAddress(address);
      customer.setShippingAddress(address);
      customer.setStatusCode(cStatus);
      customer.setName(name);
      delegator.save(customer);
      
      success.put("customer", customer);
      return success;

    } catch (GenericEntityException e) {
      Debug.logError(e, "createOrder Exception",module);
      return ServiceUtils.returnError("createOrder Exception");
    }
    
  }
}

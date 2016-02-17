package test.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.junit.Assert;
import org.junit.Test;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;





public class EntityToJsonTest {
  ObjectMapper objectMapper = new ObjectMapper();
  
  
  @Test
  public void testIt() throws JsonProcessingException {
    Person p = new Person();
    p.setId(10000);
    p.setUsername("hbh");

    for (int i = 0; i < 3; i++) {
      Car car = new Car();
      car.setId(i+1);
      car.setLicensePlate("NO-" + (i + 1));
      car.setPerson(p);
      p.getCars().add(car);
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("person", p);
    setFilter(result,objectMapper);
    String json = objectMapper.writeValueAsString(result);
    Log.d("json>" + json, EntityToJsonTest.class.getName());
    Assert.assertEquals(false, json.contains("cars"));
  }
  
  @SuppressWarnings("deprecation")
  private static void setFilter(Map<String, Object> result,ObjectMapper objectMapper) {
    //org.huihoo.foo.A(a1,a2)#org.huihoo.foo.B(b1,b2)
    String smartJsonFilter = "test.entity.Person(cars)";//AppConfigUtil.getProperty("smart.json.filter");
    if (CommUtil.isNotEmpty(smartJsonFilter)) {
      String[] jfToken = smartJsonFilter.split("#");
      for (String t : jfToken) {
        int leftBracketIdx = t.indexOf("(");
        int rightBracketIdx = t.indexOf(")");
        if (leftBracketIdx >= 0 && rightBracketIdx >= 0) {
          try {
            Class<?> clz = Class.forName(t.substring(0,leftBracketIdx));
            String[] fft = t.substring(leftBracketIdx + 1,rightBracketIdx).split(","); 
            
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("jsonFilter",
                                SimpleBeanPropertyFilter.serializeAllExcept(fft));
            objectMapper.setFilterProvider(filterProvider);
            objectMapper.addMixInAnnotations(clz, JsonFilterMixIn.class);

          } catch (ClassNotFoundException e) {
            
          }
        }
      }
    }
  }

  @JsonFilter("jsonFilter")
  private static interface JsonFilterMixIn {

  }
}


class Person extends Model{
  private int id;
  private String username;
  private String drivingLicense;
  private List<Car> cars = new ArrayList<>();

  public String getUsername() {
    return username;
  }

  public String getDrivingLicense() {
    return drivingLicense;
  }

  public List<Car> getCars() {
    return cars;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setDrivingLicense(String drivingLicense) {
    this.drivingLicense = drivingLicense;
  }

  public void setCars(List<Car> cars) {
    this.cars = cars;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}


class Car extends Model{
  private int id;
  private String licensePlate;
  private Person person;

  public String getLicensePlate() {
    return licensePlate;
  }

  public Person getPerson() {
    return person;
  }

  public void setLicensePlate(String licensePlate) {
    this.licensePlate = licensePlate;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}

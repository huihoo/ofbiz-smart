package test.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.Log;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class EntityToJsonTest {
  @SuppressWarnings("deprecation")
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

    ObjectMapper objectMapper = new ObjectMapper();
    FilterProvider filterProvider = new SimpleFilterProvider().addFilter("jsonFilter",
            SimpleBeanPropertyFilter.serializeAllExcept(new String[] {"person"}));
    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixInAnnotations(Car.class, JsonFilterMixIn.class);
    
    String json = objectMapper.writeValueAsString(result);
    Log.d("json>" + json, EntityToJsonTest.class.getName());
  }

  @JsonFilter("jsonFilter")
  private static interface JsonFilterMixIn {

  }
}


class Person {
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


class Car {
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

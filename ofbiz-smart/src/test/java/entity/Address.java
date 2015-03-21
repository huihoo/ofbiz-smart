package entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "test_address")
public class Address extends BaseModel {
  @ManyToOne
  Country countryCode;

  String line1;
  String line2;

  String city;
  String region;

  public Country getCountryCode() {
    return countryCode;
  }

  public String getLine1() {
    return line1;
  }

  public String getLine2() {
    return line2;
  }

  public String getCity() {
    return city;
  }

  public String getRegion() {
    return region;
  }

  public void setCountryCode(Country countryCode) {
    this.countryCode = countryCode;
  }

  public void setLine1(String line1) {
    this.line1 = line1;
  }

  public void setLine2(String line2) {
    this.line2 = line2;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setRegion(String region) {
    this.region = region;
  }


}

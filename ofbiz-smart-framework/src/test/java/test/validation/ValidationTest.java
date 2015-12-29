package test.validation;



import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.validation.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ValidationTest {
  private final static String TAG = ValidationTest.class.getName();

  @Test
  public void testAllInOne() {
    Log.d("Start testing...", TAG);
    ValidationUser vu = new ValidationUser();
    Map<String,List<ConstraintViolation>> constraintViolationMap = Validator.validate(vu);
    Log.d("" + constraintViolationMap, TAG);
    Assert.assertEquals(true, constraintViolationMap.containsKey("username"));

    vu.setUsername("ABC");
    constraintViolationMap = Validator.validate(vu);
    Log.d("" + constraintViolationMap, TAG);
    Assert.assertEquals(true, constraintViolationMap.containsKey("age"));
    
    vu.setAge(20);
    constraintViolationMap = Validator.validate(vu);
    Log.d("" + constraintViolationMap, TAG);
  }



  public static class ValidationUser {
    @Required
    @MinLength(6)
    @MaxLength(32)
    String username;
    
    @Min(18)
    @Max(60)
    int age;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }
  }
}

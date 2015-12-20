package org.huihoo.ofbiz.smart.validation;



import org.huihoo.ofbiz.smart.base.validation.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ValidationTest {
    private final static String TAG = ValidationTest.class.getName();

    @Test
    public void testAllInOne() {
        ValidationUser vu = new ValidationUser();
        List<ConstraintViolation> constraintViolationList = Validator.validate(vu);
        Assert.assertEquals(1,constraintViolationList.size());

        vu.setUsername("ABC");
        constraintViolationList = Validator.validate(vu);
        Assert.assertEquals(1,constraintViolationList.size());
    }




    public static class ValidationUser {
        @Required
        @MinLength(6)
        @MaxLength(32)
        String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}

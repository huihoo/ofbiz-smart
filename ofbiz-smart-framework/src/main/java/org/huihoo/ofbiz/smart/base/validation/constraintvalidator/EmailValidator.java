package org.huihoo.ofbiz.smart.base.validation.constraintvalidator;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintValidator;
import org.huihoo.ofbiz.smart.base.validation.Email;

public class EmailValidator implements ConstraintValidator<Email,CharSequence>{

    public static final java.util.regex.Pattern EMAIL_ADDRESS = java.util.regex.Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    @Override
    public void initialize(Email constraintAnnotation) {

    }

    @Override
    public boolean isValid(CharSequence value) {
        if (CommUtil.isEmpty(value)) {
            return true;
        }
        return EMAIL_ADDRESS.matcher(value).matches();
    }
}

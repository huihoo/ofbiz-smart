package org.huihoo.ofbiz.smart.base.validation;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.validation.validator.AlphanumValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.DecimalMaxValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.DecimalMinValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.DecimalRangeValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.DigitsValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.EmailValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.MaxLengthValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.MaxValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.MinLengthValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.MinValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.NotNullValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.NullValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.PatternValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.RequiredValidator;
import org.huihoo.ofbiz.smart.base.validation.validator.UrlValidator;

import ognl.Ognl;
import ognl.OgnlException;

public class Validator {
  private final static String TAG = Validator.class.getName();

  private final static Object[] EMPTY_ARGS = new Object[] {};

  private final static Class<?>[] EMPTY_CLAZZ = new Class<?>[] {};

  private final static Map<Class<? extends Annotation>, List<? extends Class<?>>> builtinConstraints;

  public static final String[] EXCLUDE_INCLUDE_NAMES = {"serialVersionUID", "_EBEAN_", "_ebean","_ctx","ebean","action.config."};

  static {
    Map<Class<? extends Annotation>, List<? extends Class<?>>> tmpConstraints = new HashMap<>();
    tmpConstraints.put(Required.class, Collections.singletonList(RequiredValidator.class));
    tmpConstraints.put(Null.class, Collections.singletonList(NullValidator.class));
    tmpConstraints.put(NotNull.class, Collections.singletonList(NotNullValidator.class));
    tmpConstraints.put(Min.class, Collections.singletonList(MinValidator.class));
    tmpConstraints.put(Max.class, Collections.singletonList(MaxValidator.class));
    tmpConstraints.put(DecimalMin.class, Collections.singletonList(DecimalMinValidator.class));
    tmpConstraints.put(DecimalMax.class, Collections.singletonList(DecimalMaxValidator.class));
    tmpConstraints.put(DecimalRange.class, Collections.singletonList(DecimalRangeValidator.class));
    tmpConstraints.put(Digits.class, Collections.singletonList(DigitsValidator.class));
    tmpConstraints.put(MinLength.class, Collections.singletonList(MinLengthValidator.class));
    tmpConstraints.put(MaxLength.class, Collections.singletonList(MaxLengthValidator.class));
    tmpConstraints.put(Email.class, Collections.singletonList(EmailValidator.class));
    tmpConstraints.put(Url.class, Collections.singletonList(UrlValidator.class));
    tmpConstraints.put(Alphanum.class, Collections.singletonList(AlphanumValidator.class));
    tmpConstraints.put(Pattern.class, Collections.singletonList(PatternValidator.class));
    builtinConstraints = Collections.unmodifiableMap(tmpConstraints);
  }

  public static Map<String,List<ConstraintViolation>> validate(Object target) {
    return validate(target, ValidateProfile.ALL);
  }

  public static Map<String,List<ConstraintViolation>> validate(Object target, ValidateProfile profile) {
    Map<String,List<ConstraintViolation>> constraintViolationMap = new LinkedHashMap<>();
    if (target == null) {
      return constraintViolationMap;
    }
    Log.d("Validation Object : " + target, TAG);
    try {
      //FIXME superClass field validation?
      Field[] allFields = getFields(target);
      for (Field f : allFields) {
        String fieldName = f.getName();
        if (ignore(fieldName)) {
          continue;
        }

        String fieldTypeName = f.getType().getName();
        Object value = null;
        try {
          value = Ognl.getValue(fieldName, target);
          Log.d("field(%s,%s,%s)", TAG, fieldName, fieldTypeName, value);
        } catch (OgnlException e) {
          Log.w("Unable to get property %s value", TAG, fieldName);
          continue;
        }
        List<ConstraintViolation> tmpList = validateField(profile, f, value);
        if (CommUtil.isNotEmpty(tmpList)) {
          constraintViolationMap.put(fieldName, validateField(profile, f, value));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.w("Unable to validate for object[%s] : %s", TAG, target, e.getMessage());
    }

    return constraintViolationMap;
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  private static List<ConstraintViolation> validateField(ValidateProfile profile, Field f,Object value) {
    List<ConstraintViolation> constraintViolations = new ArrayList<>();
    Object[] annos = null;
    try {
      String fieldName = f.getName();
      annos = f.getAnnotations();

      for (Object o : annos) {
        Annotation anno = (Annotation) o;
        List<Class<?>> constraintValidators = (List<Class<?>>) builtinConstraints.get(anno.annotationType());
        if (constraintValidators != null) {
          ValidateProfile tmpProfile = null;

          Method profileMethod = anno.getClass().getMethod("profile", EMPTY_CLAZZ);
          if (profileMethod != null) {

            tmpProfile = (ValidateProfile) profileMethod.invoke(anno, EMPTY_ARGS);
            if (tmpProfile != ValidateProfile.ALL && tmpProfile != profile) {
              continue;
            }
          }


          for (Class<?> cvClazz : constraintValidators) {
            ConstraintValidator cv = (ConstraintValidator) cvClazz.newInstance();
            cv.initialize(anno);
            if (cv.isValid(value)) {
              continue;
            }
            if (anno instanceof Required) {
              Required tmpAnno = (Required) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Min) {
              Min tmpAnno = (Min) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Max) {
              Max tmpAnno = (Max) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof DecimalMax) {
              DecimalMax tmpAnno = (DecimalMax) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof DecimalMin) {
              DecimalMin tmpAnno = (DecimalMin) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof DecimalRange) {
              DecimalRange tmpAnno = (DecimalRange) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof MinLength) {
              MinLength tmpAnno = (MinLength) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof MaxLength) {
              MaxLength tmpAnno = (MaxLength) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Range) {
              Range tmpAnno = (Range) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Email) {
              Email tmpAnno = (Email) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Url) {
              Url tmpAnno = (Url) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Pattern) {
              Pattern tmpAnno = (Pattern) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof NotNull) {
              NotNull tmpAnno = (NotNull) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Null) {
              Null tmpAnno = (Null) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            } else if (anno instanceof Digits) {
              Digits tmpAnno = (Digits) anno;
              ConstraintViolation consViol = new ConstraintViolation(fieldName, tmpAnno.message(), value);
              constraintViolations.add(consViol);
            }
          }
        }
      }

    } catch (InstantiationException e) {
      Log.w("Unable to validate for field[%s] : %s", TAG, f, e.getMessage());
    } catch (IllegalAccessException e) {
      Log.w("Unable to validate for field[%s] : %s", TAG, f, e.getMessage());
    } catch (NoSuchMethodException e) {
      Log.w("Unable to validate for field[%s] : %s", TAG, f, e.getMessage());
    } catch (InvocationTargetException e) {
      Log.w("Unable to validate for field[%s] : %s", TAG, f, e.getMessage());
    }
    return constraintViolations;
  }
  
  
  private static Field[] getFields(Object target) {
    int len = 0;
    Field[] firstParentFields = null;
    Field[] secondParentFields = null;
    //first level superClass
    Class<?> superClazz = target.getClass().getSuperclass();
    if (superClazz != null) {
      firstParentFields = superClazz.getDeclaredFields();
      len += firstParentFields.length;
      
      //second level superClass(max hierarchy level is 2.)
      Class<?> secondSupperClass = superClazz.getSuperclass();
      if (secondSupperClass != null) {
        secondParentFields = secondSupperClass.getDeclaredFields();
        len += secondParentFields.length;
      }
    }
       
    Field[] fields = target.getClass().getDeclaredFields();
    len += fields.length;
    
    Field[] allFields = new Field[len];
    
    System.arraycopy(fields, 0, allFields, 0, fields.length);
    
    if (firstParentFields != null) {
      System.arraycopy(firstParentFields, 0, allFields, fields.length, firstParentFields.length);
    }
    if (secondParentFields != null) {
      System.arraycopy(secondParentFields, 0, allFields, fields.length + firstParentFields.length, secondParentFields.length);
    }
    
    return allFields;
  }
  
  private static boolean ignore(String fieldName) {
    for (String excludeName : EXCLUDE_INCLUDE_NAMES) {
      if (fieldName.equals(excludeName) || fieldName.startsWith(excludeName)) {
        return true;
      }
    }
    return false;
  }
}

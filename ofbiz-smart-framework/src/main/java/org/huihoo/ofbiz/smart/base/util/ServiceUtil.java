package org.huihoo.ofbiz.smart.base.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.validation.ConstraintViolation;
import org.huihoo.ofbiz.smart.entity.Delegator;

public class ServiceUtil {
  public static final String RESPONSE_MESSAGE = "message";
  public static final String RESPOND_SUCCESS = "success";
  public static final String RESPOND_ERROR = "error";

  public static Delegator getDelegator(Map<String, Object> ctx) {
    if (ctx == null) {
      return null;
    }
    return (Delegator) ctx.get(C.CTX_DELETAGOR);
  }
  
  public static boolean isSuccess(Map<String, Object> result) {
    if (result == null || result.containsKey(RESPOND_ERROR)) {
      return false;
    }
    return result.containsKey(RESPOND_SUCCESS);
  }


  public static boolean isError(Map<String, Object> results) {
    if (results == null || results.containsKey(RESPOND_ERROR)) {
      return true;
    }
    return false;
  }

  public static Map<String, Object> returnSuccess() {
    return returnMessage(RESPOND_SUCCESS, "SUCCESS");
  }

  public static Map<String, Object> returnProplem(String code, String message,
                                                  List<ConstraintViolation> constraintViolations) {
    Map<String, Object> result = new HashMap<>();
    if (code != null) {
      result.put(RESPOND_ERROR, code);
    }
    if (message != null) {
      result.put(RESPONSE_MESSAGE, message);
    }
    if (CommUtil.isNotEmpty(constraintViolations)) {
      result.put(C.RESPOND_VALIDATION_ERRORS, constraintViolations);
    }
    return result;
  }

  public static Map<String, Object> returnProplem(String code, String message) {
    return returnProplem(code, message, new ArrayList<ConstraintViolation>());
  }


  public static Map<String, Object> returnMessage(String code, String message) {
    Map<String, Object> result = new HashMap<>();
    if (code != null) {
      result.put(RESPOND_SUCCESS, code);
    }
    if (message != null) {
      result.put(RESPONSE_MESSAGE, message);
    }
    return result;
  }
  
}

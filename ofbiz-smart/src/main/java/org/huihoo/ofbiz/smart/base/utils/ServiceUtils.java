package org.huihoo.ofbiz.smart.base.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.validation.FieldError;





public class ServiceUtils {
  public static final String RESPONSE_MESSAGE = "message";
  public static final String RESPOND_SUCCESS = "success";
  public static final String RESPOND_ERROR = "error";
  public static final String RESPOND_FAIL = "fail";
  
  
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


public static String buildErrorString(List<FieldError> errors) {
    StringBuffer sb = new StringBuffer();
    for (FieldError fieldError : errors) {
        sb.append(fieldError.getName() + ":" + fieldError.getMessage()).append("\r\n");
    }
    return sb.toString();
}


public static Map<String, Object> returnError(String errorMessage) {
    return returnProplem(RESPOND_ERROR, errorMessage);
}


public static Map<String, Object> returnSuccess() {
    return returnMessage(RESPOND_SUCCESS, "SUCCESS");
}


public static Map<String, Object> returnSuccess(String successMessage) {
    return returnMessage(RESPOND_SUCCESS, successMessage);
}


public static Map<String, Object> returnProplem(String code, String message) {
    Map<String, Object> result = new HashMap<String, Object>();
    if (code != null)
        result.put(RESPOND_ERROR, code);
    if (message != null)
        result.put(RESPONSE_MESSAGE, message);
    return result;
}


public static Map<String, Object> returnMessage(String code, String message) {
    Map<String, Object> result = new HashMap<String, Object>();
    if (code != null)
        result.put(RESPOND_SUCCESS, code);
    if (message != null)
        result.put(RESPONSE_MESSAGE, message);
    return result;
}
}

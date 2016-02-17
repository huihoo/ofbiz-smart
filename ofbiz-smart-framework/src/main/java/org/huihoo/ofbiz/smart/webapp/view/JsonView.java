package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.AppConfigUtil;
import org.huihoo.ofbiz.smart.base.util.CommUtil;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class JsonView implements View {
  private volatile ObjectMapper objectMapper = new ObjectMapper();
  private volatile boolean setFlag = false;
  
  @Override
  public String getContentType() {
    return C.JSON_CONTENT_TYPE;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) throws ViewException {
    response.setContentType(getContentType());
    try {
      if (!setFlag) {
        String smartJsonFilter = AppConfigUtil.getProperty("smart.json.filter");
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
                //ignore
              }
            }
          }
          setFlag = true;
        }
      }
      response.getWriter().write(objectMapper.writeValueAsString(model));
      response.getWriter().flush();
    } catch (Exception e) {
      throw new ViewException(e);
    }
  }
  
  
  @JsonFilter("jsonFilter")
  private static interface JsonFilterMixIn {
    
  }
  
}

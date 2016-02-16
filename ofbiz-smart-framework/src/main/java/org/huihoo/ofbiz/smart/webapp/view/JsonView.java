package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class JsonView implements View {

  @Override
  public String getContentType() {
    return C.JSON_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) throws ViewException {
    response.setContentType(getContentType());
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      FilterProvider filterProvider = new SimpleFilterProvider().addFilter("jsonFilter",
        SimpleBeanPropertyFilter.serializeAllExcept(new String[]{}));
      objectMapper.setFilterProvider(filterProvider);
      objectMapper.addMixInAnnotations(model.getClass(), JsonFilterMixIn.class);
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

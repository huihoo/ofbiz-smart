package test.webapp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.webapp.DispatchServlet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class WebWithJettyTest {
  private static final String TAG = WebWithJettyTest.class.getName();
  protected static Server server;

  @Before
  public void setUp() throws Exception {
    Log.i("It's starting server...", TAG);
    server = new Server(8080);
    server.setStopAtShutdown(true);
    //Custom handler for processing...
    ContextHandler context = new ServletContextHandler();
    
    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(DispatchServlet.class, "*.htm");
    handler.addServletWithMapping(DispatchServlet.class, "/api/router");
    handler.addServletWithMapping(DispatchServlet.class, "/rest");
    
    context.setHandler(handler);
    context.setResourceBase(".");
    context.setContextPath("");
    
    server.setHandler(context); 
    server.start();
  }

  @Test
  public void testGetContentOk() throws Exception {
    OkHttpClient client = new OkHttpClient();
    com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
            .url("http://127.0.0.1:8080/testGetContentOk.htm").build();

    Response response = client.newCall(request).execute();

    String result = response.body().string();
    Log.i("result >" + result, TAG);
    Assert.assertEquals(true, result.contains("It works"));
  }
  
  @Test
  public void testUploadFileOk() throws Exception {
    OkHttpClient client = new OkHttpClient();
    File file = new File(FlexibleLocation.resolveLocation("/java.jpg").getFile());
    RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(),RequestBody.create(MediaType.parse("image/jpeg"), file))
                .addFormDataPart("some-field", "some-value")
                .build();
    
    com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("http://127.0.0.1:8080/testUploadFileOk.htm?url-filed=url-value")
                .post(requestBody)
                .build();

    Response response = client.newCall(request).execute();

    String result = response.body().string();
    Log.i("result >" + result, TAG);
    Assert.assertEquals(true, result.contains("SUCCESS"));
  }
  
  
  @Test
  public void testRequestApiOk() throws Exception {
    OkHttpClient client = new OkHttpClient();
    File file = new File(FlexibleLocation.resolveLocation("/java.jpg").getFile());
    RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(),RequestBody.create(MediaType.parse("image/jpeg"), file))
                .addFormDataPart("some-field", "some-value")
                .build();
    String appKey = "201618181818";
    String method = "smart.service.not.found";
    String format = "json";
    String signMethod = "hmac";
    String timestamp = "" + System.currentTimeMillis();
    Map<String,String> paraMap = new TreeMap<>();
    paraMap.put("appKey", appKey);
    paraMap.put("method", method);
    paraMap.put("format", format);
    paraMap.put("signMethod", signMethod);
    paraMap.put("timestamp", timestamp);
    String sign = createSignString(paraMap,signMethod);
    
    com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("http://127.0.0.1:8080/api/router?appKey="+appKey+"&method="+method+"&format="+format+"&signMethod="+signMethod+"&timestamp="+timestamp+"&sign="+sign)
                .post(requestBody)
                .build();

    Response response = client.newCall(request).execute();

    String result = response.body().string();
    Log.i("result >" + result, TAG);
    Assert.assertEquals(true, result.contains("SERVICE_NOT_FOUND"));
    
    String fromChannel = "WEB_CHANNEL";
    String userId = "10000";
    String paymentMethod = "ALI_PAY";
    
    method = "org.huihoo.order.createSuccess";
    paraMap.put("method", method);
    paraMap.put("fromChannel", fromChannel);
    paraMap.put("userId", userId);
    paraMap.put("paymentMethod", paymentMethod);
    sign = createSignString(paraMap,signMethod);
    request = new com.squareup.okhttp.Request.Builder()
            .url("http://127.0.0.1:8080/api/router?appKey="+appKey
              +"&method="+method
              +"&format="+format
              +"&signMethod="+signMethod
              +"&timestamp="+timestamp
              +"&sign="+sign
              +"&fromChannel="+fromChannel
              +"&userId="+userId
              +"&paymentMethod="+paymentMethod
              )
            .post(requestBody)
            .build();
    response = client.newCall(request).execute();
    result = response.body().string();
    Log.i("result >" + result, TAG);
    Assert.assertEquals(true, result.contains("SUCCESS"));
    
    
    
    fromChannel = "WEB_CHANNEL";
    userId = "10000";
    paymentMethod = "ALI_PAY";
    
    method = "org.huihoo.order.createSuccess.auth";
    paraMap.put("method", method);
    paraMap.put("fromChannel", fromChannel);
    paraMap.put("userId", userId);
    paraMap.put("paymentMethod", paymentMethod);
    sign = createSignString(paraMap,signMethod);
    request = new com.squareup.okhttp.Request.Builder()
            .url("http://127.0.0.1:8080/api/router?appKey="+appKey
              +"&method="+method
              +"&format="+format
              +"&signMethod="+signMethod
              +"&timestamp="+timestamp
              +"&sign="+sign
              +"&fromChannel="+fromChannel
              +"&userId="+userId
              +"&paymentMethod="+paymentMethod
              )
            .post(requestBody)
            .build();
    response = client.newCall(request).execute();
    result = response.body().string();
    Log.i("result >" + result, TAG);
    Assert.assertEquals(true, result.contains("ACCESS_TOKEN_REQUIRED"));
    
    
    
    fromChannel = "WEB_CHANNEL";
    userId = "10000";
    paymentMethod = "ALI_PAY";
    String accessToken = "af4fb8750ef7140947e41b48b3fbb815";
    method = "org.huihoo.order.createSuccess.auth";
    paraMap.put("method", method);
    paraMap.put("fromChannel", fromChannel);
    paraMap.put("userId", userId);
    paraMap.put("accessToken", accessToken);
    paraMap.put("paymentMethod", paymentMethod);
    sign = createSignString(paraMap,signMethod);
    request = new com.squareup.okhttp.Request.Builder()
            .url("http://127.0.0.1:8080/api/router?appKey="+appKey
              +"&method="+method
              +"&format="+format
              +"&signMethod="+signMethod
              +"&timestamp="+timestamp
              +"&sign="+sign
              +"&fromChannel="+fromChannel
              +"&userId="+userId
              +"&paymentMethod="+paymentMethod
              +"&accessToken="+accessToken
              )
            .post(requestBody)
            .build();
    response = client.newCall(request).execute();
    result = response.body().string();
    Log.i("result >" + result, TAG);
    Assert.assertEquals(true, result.contains("SUCCESS"));
  }
  
  private String createSignString(Map<String,String> params,String signMethod) {
    StringBuilder sb = new StringBuilder();
    String appSecret = "df4fb8750ef7130947e41b48b3fbb815";
    Iterator<Entry<String, String>> iter = params.entrySet().iterator();
    while (iter.hasNext()) {
      Entry<String, String> entry = iter.next();
      sb.append(entry.getKey()).append(entry.getValue());
    }
    String sign = "";
    if ("md5".equals(signMethod)) {
      sign = CommUtil.md5(appSecret + sb.toString() + appSecret);
    } else if ("hmac".equals(signMethod)) {
      sign = CommUtil.hmacSha1(sb.toString(), appSecret);
    }
    return sign;
  }

  @SuppressWarnings("unused")
  private class MockGetContentOkHandler extends AbstractHandler {
    @Override
    public void handle(String s, Request req, HttpServletRequest httpServletRequest,
            HttpServletResponse response) throws IOException, ServletException {
      OutputStream out = response.getOutputStream();
      ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();

      writer.write("It works");
      writer.flush();
      response.setContentLength(writer.size());
      writer.writeTo(out);
      out.flush();
      writer.close();
      req.setHandled(true);
    }
  }

  @After
  public void tearDown() throws Exception {
    Log.i("It's stopping server...", TAG);
    server.stop();
  }
}

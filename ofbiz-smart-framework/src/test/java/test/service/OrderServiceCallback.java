package test.service;

import java.util.Map;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.service.ServiceCallback;

public class OrderServiceCallback implements ServiceCallback {
  private final static String TAG = OrderServiceCallback.class.getName();

  @Override
  public void receiveEvent(Map<String, Object> ctx, Map<String, Object> result) {
    Log.d("receiveEvent: ctx->" + ctx + " result->" +result, TAG);
  }

  @Override
  public void receiveEvent(Map<String, Object> ctx, Throwable t) {

  }

}

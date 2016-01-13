# ServiceCallback

> 实现服务回调只需实现**ServiceCallback**接口即可。

如下所示：

```java
public class OrderServiceCallback implements ServiceCallback {
  private final static String TAG = OrderServiceCallback.class.getName();

  @Override
  public void receiveEvent(Map<String, Object> ctx, Map<String, Object> result) {
    Log.d("服务成功调用后执行: ctx->" + ctx + " result->" +result, TAG);
  }

  @Override
  public void receiveEvent(Map<String, Object> ctx, Throwable t) {
    Log.d("服务发生异常时执行" + t, TAG);
  }
}
```
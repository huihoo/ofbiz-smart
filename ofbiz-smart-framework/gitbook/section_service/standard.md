# StandardJavaEngine

> 标准的服务引擎，负责普通的Java服务类的执行。

> 服务引擎名称固定为 **java**。

> 指定服务类的全路径及具体的方法，然后通过**Java Method Invoke**来动态执行。

> 所有执行过的服务类会被缓存，以便下次的服务调用不用在动态加载。

> 服务执行成功后，会返回**Map**；不成功直接抛出 **GenericServiceException**。


如下所示的服务类，此类服务由该服务引擎负责执行。

```java
//服务类
@Service
public class OrderService {
  private final static String TAG = OrderService.class.getName();
  
  @ServiceDefinition(
    name = "createOrder"
    ,description = "创建订单"
    ,inParameters = {
        @InParameter(name = "fromChannel",required = true,description = "订单来源")
       ,@InParameter(name = "userId",required = true,description = "订单创建者ID",type = Long.class)
       ,@InParameter(name = "paymentMethod",required = true,description = "支付方式") 
    }
    ,outParameters = {
        @OutParameter(name = "orderId",required = true,description = "创建成功的订单编号") 
       ,@OutParameter(name = "grandTotal",required = true,description = "订单总价",type = BigDecimal.class)  
    }
    ,responseJsonExample = "{'orderId','20151231001','grandTotal',300.00}"
  )
  public static Map<String,Object> createOrder(Map<String,Object> ctx) {
    try {
      Map<String, Object> resultMap = ServiceUtil.returnSuccess();
      //TODO
      return resultMap;
    } catch(Exception e) {
      Log.e(e, "OrderService.createOrder occurs exception.", TAG);
      return ServiceUtil.returnProplem("SERVICE_EXCEPTION", "服务执行发生异常");
    }
  }
}

//调用服务，实际的服务调用由StandardJavaEngine来执行
Map<String, Object> ctx = CommUtil.toMap(C.CTX_DELETAGOR, delegator);
ServiceDispatcher serviceDispatcher = new ServiceDispatcher(delegator);
Map<String,Object> resultMap = serviceDispatcher.runSync("createOrder", ctx);
    
```


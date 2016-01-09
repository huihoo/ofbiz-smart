# 参数校验

> 当服务明确定义了要校验的参数，框架会对参数严格的验证，只有通过验证，才能调用服务或是返回服务成功调用结果。

比如如下的服务定义：

```java
@ServiceDefinition(
    name = "createOrder"
    ,description = "创建订单"
    ,parameters = {
        @Parameter(name = "fromChannel",optinal = false,mode="IN", description = "订单来源")
       ,@Parameter(name = "userId",type = Long.class,optinal = false,mode="IN",description = "订单创建者ID")
       ,@Parameter(name = "paymentMethod",optinal = false,mode="IN",description = "支付方式") 
       ,@Parameter(name = "orderId",optinal = false,mode="OUT",description = "创建成功的订单编号") 
       ,@Parameter(name = "grandTotal",type = BigDecimal.class,optinal = false,mode="OUT",description = "订单总价") 
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
```
如上所示

>输入参数: fromChannel，必传；userId，类型为Long,必传；paymentMethod，必传

>输出参数: orderId,必须返回；grandTotal,类型为BigDecimal,必须返回

如果在调用上面的**createOrder**服务，三个输入参数都没传时，框架校验过后，返回的**Map**，如下所示：

```java
{
 message=Service input or output parameters is invalid.,  error=SERVICE_PARAMETERS_INVALID, 
 validation_errors=
 {
  createOrder=[
    ConstraintViolation{
     fieldName='createOrder.fromChannel’, 
     filedMessage='Input parameter[fromChannel] must be setting.’
    },        
    ConstraintViolation{
     fieldName='createOrder.userId’, 
     filedMessage='Input parameter[userId] must be setting.’
    }, 
    ConstraintViolation{
     fieldName='createOrder.paymentMethod’,      
     filedMessage=‘Input parameter[paymentMethod] must be setting.’
   }]
  }
 }
```
可以看到，返回的校验结果**validation_errors**也是一个**Map**，大小是1,唯一的key为服务的名称，该key对应的值为一个[**ConstraintViolation**](../section_validation/index.md)集合。


# 用户认证

//TODO
# ServiceDispatcher

> ServiceDispatcher是所有服务执行的入口，它根据具体的服务，将其转发至合适的服务引擎来实际执行。

> ServiceDispatcher根据服务的事务配置，进行事务控制。

> ServiceDispatcher初始化时，会主动扫描服务所在的包路径，自动加载服务类。

> ServiceDispatcher在服务执行前进行验证，认证等操作(如果有必要），在服务执行完成后，进行验证，国际化返回码等操作(如果有必要)。

> ServiceDispatcher会统计每个服务的执行时间，根据配置的service.slowtime.milliseconds属性，会单独进行日志记录。

> ServiceDispatcher是一个比较重的对象，但它不是以单例模式设计的，所以要求应用方自行管理该对象，强烈建议应用中中仅存在唯一实例。

> ServiceDispatcher负责在服务调用成功和失败时，执行具体的服务回调。

调用示例：

```java
//实例化ServiceDispatcher
ServiceDispatcher serviceDispatcher = new ServiceDispatcher(delegator);

//构建服务执行上下文参数ctx
//指定服务名和参数ctx，执行服务
Map<String,Object> resultMap = serviceDispatcher.runSync("service001", ctx);

resultMap = serviceDispatcher.runSync("service002", ctx);

```
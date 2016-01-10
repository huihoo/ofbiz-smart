# 服务上下文

> 它是一个**Map**对象实例，任何需要在服务执行时用到的参数/对象，都可以**put**进去

> **Map**对象实例保存的上下文信息，在具体的服务执行环境中有所不同

> **Map**对象实例保存了服务的输入参数，还保存了上层调用环境中(如HttpServletRequest)的对象，以便在服务执行中，可以直接获取(如果有必要)

如下所示

```java

//设置服务执行上下文**Map**对象
Map<String,Object> ctx = new LinkedHashMap<>();
ctx.put("httpServletRequest",request);
ctx.put("username","hbh");

//执行服务
serviceDispatcher.runSync("serviceName",ctx);

```

如上所示：

服务执行上下文**Map**对象**put**了服务调用所需要的各种参数。


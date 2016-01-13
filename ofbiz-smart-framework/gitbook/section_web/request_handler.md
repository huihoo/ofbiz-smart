# RequestHandler

> 请求处理器接口，负责请求的实际处理。

> 根据[DispatchServlet](dispatch_servlet.md#dispatchservlet)中的示例配置将对应的请求转发给实际的
> 请求处理器来处理。

如下所示：

1. /api.htm 由 [API请求处理器](req_api.md)处理 

2. /rest.htm 由[Restful请求处理器](req_restfull.md)处理

3. /doc.htm 由[Doc请求处理器](req_doc.md)处理

4. 其它由 [默认请求处理器](req_default.md)处理

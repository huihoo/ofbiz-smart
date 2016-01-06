# 服务的定义

> 通过注解的方式来定义，选用注解的形式来定义，是在实际的开发中，发现注解比Xml配置更加的方便。

> 凡是显示定义的服务类，都由 [标准Java服务引擎](../section_service/standard.md) 来执行。

{% em color="#ff0000" %}
注意：除了用注解来标识服务类和具体的服务方法外，对服务方法以下要求:
{% endem %}
1. 方法必须是 **static**的

2. 有且只有一个类型为**Map<String,Object>**的参数

3. 返回参数类型只能为**Map<String,Object>**类型



## Service 

> Service注解标识指定的类是一个服务类，服务引擎扫描指定路径时，仅加载标识为服务的类。


示例：

```java
@Service
public class UserService {
  //...
}
```


## ServiceDefinition

> ServiceDefinition注解标识具体的服务方法，一个服务类中标识了该注解的方法为服务方法。

> ServiceDefinition注解有很多用来定义服务的属性组成。

服务定义属性：

名称                                   | 描述                                    
:-----------:| :-----------
name         | 服务的名称（应用中是唯一的） ，必须指定
type         | 服务的类型，默认为java
description  | 服务的描述信息，用来描述服务具体能做什么
transaction  | 是否启用事务，为true时表示开启事务，默认为false
persist|是否需要持久化(默认为true)，为true时，delegator不能为空，为false时，不需要指定delegator,适用在不需要操作数据库的场景中 
requireAuth | 是否需要身份认证，true需要，false不需要，默认为false
export| 是否对外提供远程调用，true提供，false不提供，默认为false
entityName | 指定的实体名称(如果有)
callback |服务回调数组（如果有）
inParameters |输入参数定义数组（如果有）
outParameters |输出参数定义数组（如果有）
responseJsonExample |服务返回为json时的对应json字符串示例（如果有）
responseXmlExample |服务返回为xml时的对应xml字符串示例（如果有）

## InParameter

> InParameter注解定义了服务的输入参数

> 如果服务定义了具体的输入参数，服务引擎会对输入参数进行严格的验证，所有参数通过验证后，服务才被调用。

定义属性:

名称                                   | 描述                                    
:-----------:| :-----------
name         | 参数的名称
description  | 参数的描述
required     | 参数是否必需，true必须，false非必须，默认为false
defaultValue | 参数默认值，默认为空
type         | 参数Class类型，默认为**String.class**

## OutParameter

> OutParameter注解定义了服务的输出参数

> 如果服务定义了具体的输出参数，服务引擎会对输出参数进行严格的验证，所有参数通过验证后，才返回成功的服务调用结果。

定义属性:

名称                                   | 描述                                    
:-----------:| :-----------
name         | 参数的名称
description  | 参数的描述
required     | 参数是否必需，true必须，false非必须，默认为false
defaultValue | 参数默认值，默认为空
type         | 参数Class类型，默认为**String.class**

服务定义示例：

```java
@Service
public class UserService {

  @ServiceDefinition(
    name = "userEnter"
    ,description = "用户登录"
    ,inParameters = {
        @InParameter(name = "username",required = true,description = "用户名")
       ,@InParameter(name = "password",required = true,description = "用户密码")
    }
    ,outParameters = {
        @OutParameter(name = "userId",required = true,description = "用户ID")  
    }
    ,responseJsonExample = "{'userId','10000'}"
  )
  public static Map<String,Object> userEnter(Map<String,Object> ctx) {
    //your code goes here.
  	return null;
  }
  
}
```
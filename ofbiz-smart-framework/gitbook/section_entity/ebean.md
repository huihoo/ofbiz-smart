# EbeanDelegator

基于[Ebean](https://github.com/ebean-orm/avaje-ebeanorm)的[Delegator]()实现，也是框架默认实现，未来会支持更多的实现，比如[MyBatis](https://github.com/mybatis/mybatis-3)。

Ebean的特点：


1. [Ebean](https://github.com/ebean-orm/avaje-ebeanorm)是一个ORM框架，它的主要特点是轻量，稳定，性能优异。同时[Play Framework](http://www.playframework.org/)框架也将它做为标配的 ORM 实现。
虽然也是ORM框架，但跟传统的 ORM 框架，比如 JPA,JDO,Hibernate等框架相比，最大的不同是，它没有使用 'Persistence Context'(持久化上下文) 或 'Session'(会话)将对象保存至数据库。
它不需要 对实体进行 attach/detach 等操作，也不需要管理 JPA EntityManager,JDO PersistenceManager,Hibernate SessionFactory等重量级会话对象。
 
2. Ebean虽然很轻，但该有的功能一个不少，完全能满足各种不同的业务需求。它还支持标准的JPA1.0 规范，很容易从其它ORM框架上过渡过来。 
  
3. Ebean的API也很友好，代码规范，容易扩展。

# Entity Enhanced

实体动态增强

由于**Delegator**的默认实现**EbeanDelegator**，构建在**Ebean**之上。它要求在调用API之前，要对实体进行动态加强，也就是动态对实体进行转换操作，比如增加实体的toString(),hashCode()方法，
在实体中增加 **Ebean**需要的标识字段，这些字段一般以**ebean**开头，以及一些方法等等。

实体的动态加强特性，基于**Java Agent**机制。同一虚拟机有些限制，比如已经动态加强并加载到**Java虚拟机**的实体类，会影响到其它实体类加载到该虚拟机。会造成，同一个tomcat无法运行多个 webapp应用。所以仅在开发模式下，**OFBiz Smart**才会使用**Ebean**的这种方式来动态加强实体类。如下面的代码所示：

```java
// 仅在非生产环境下通过这种方式动态增强实体特性
String profile = appConfig.getProperty("profile");
if (!C.PROFILE_PRODUCTION.equals(profile)) {
  String entityPackages = appConfig.getProperty(C.ENTITY_SCANNING_PACKAGES);
  String p = "avaje-ebeanorm-agent","debug=1;packages="+entityPackages;
  if (!AgentLoader.loadAgentFromClasspath(p)) {
    Log.i("avaje-ebeanorm-agent not found in classpath - not dynamically loaded",TAG);
  }
}
    
``` 

所以在生产环境下面，是不会用这种方式来转换实体类的。

在生产环境下面，使用**Maven Plugin**的形式预先生成已经转换的实体类。该**PlugIn**为:

```java
 <plugin>
    <groupId>org.avaje.ebeanorm</groupId>
	<artifactId>avaje-ebeanorm-mavenenhancer</artifactId>
	<version>4.7.1</version>
	<executions>
		<execution>
			<id>main</id>
			<phase>process-test-classes</phase>
			<configuration>
			<packages>test.entity.**</packages>
			<transformArgs>debug=1</transformArgs>
			</configuration>
			<goals>
				<goal>enhance</goal>
			</goals>
		</execution>
	</executions>
</plugin>
			
```

使用命令 ** mvn clean install ** 构建项目。
  
  
  
  
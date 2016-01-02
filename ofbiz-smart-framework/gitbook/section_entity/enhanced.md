由于**Delegator**的默认实现**EbeanDelegator**，构建在**Ebean**之上。它要求在调用API之前，要对实体进行动态加强，也就是动态对实体进行转换操作，比如增加实体的toString(),hashCode()方法，
在实体中增加 **Ebean**需要的标识字段，这些字段一般以**ebean**开头，以及一些方法等等。

实体的动态加强特性，基于**Java Agent**机制。同一虚拟机有些限制，比如已经动态加强并加载到**Java虚拟机**的实体类，会影响到其它实体类加载到该虚拟机。会造成，同一个tomcat无法运行多个 webapp应用。所以仅在开发模式下，**OFBiz Smart**才会使用**Ebean**的这种方式来动态加强实体类。如下面的代码所示：

```
// 仅在非生产环境下通过这种方式动态增强实体特性
String profile = applicationProps.getProperty("profile");
    if (!C.PROFILE_PRODUCTION.equals(profile)) {
      String entityPackages = applicationProps.getProperty(C.ENTITY_SCANNING_PACKAGES);
      if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent","debug=1;packages="+entityPackages)) {
          Log.i("avaje-ebeanorm-agent not found in classpath - not dynamically loaded",TAG);
      }
    }
    
``` 

所以在生产环境下面，是不会用这种方式来转换实体类的。

在生产环境下面，使用**Maven Plugin**的形式预先生成已经转换的实体类。该**PlugIn**为:

```
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
# 应用配置

## properties属性文件

> 应用的配置文件为标准的properties属性文件

> 配置文件命令必须以application开头

> application.properties为应用的主配置文件

如下所示(application.properties)

```
profile=develop
active.profiles=test,develop,production

```

   > profile表示当前应用的运行环境

   > active.profiles表示当前应用支持的运行环境，多个以逗号隔开

   > active.profiles指定的有效profile必然一一对应有指定的配置文件

   > 如下所示：

   > (application-test.properties)
	
   > ```
   > action.config.basepath=./
   > entity.scanning.packages=test.entity.**
   > service.scanning.resource.names=test.service
   > service.slowtime.milliseconds=1000
   > #service.scanning.transaction=insert*,update*,delete*,save*

   > # generate DDL files
   > ebean.ddl.generate=true
   > # run ddl drops and recreates tables
   > ebean.ddl.run=true
   > ebean.uuidStoreAsBinary=true
   > ebean.databaseSequenceBatchSize=1
   > ebean.debug.sql=true
   > ebean.debug.lazyload=false

   > datasource.default=h2
   > datasource.h2.provider=org.huihoo.ofbiz.smart.entity.DefaultDataSourceProvider
   > datasource.h2.username=sa
   > datasource.h2.password=
   > datasource.h2.databaseUrl=jdbc:h2:mem:tests
   > datasource.h2.databaseDriver=org.h2.Driver
   > ```

   > (application-develop.properties)

   > ```
   > action.config.basepath=./
   > entity.scanning.packages=test.entity.**
   > service.scanning.resource.names=test.service
   > service.slowtime.milliseconds=1000
   > #service.scanning.transaction=insert*,update*,delete*,save*

   > # generate DDL files
   > ebean.ddl.generate=false
   > # run ddl drops and recreates tables
   > ebean.ddl.run=false
   > ebean.uuidStoreAsBinary=true
   > ebean.databaseSequenceBatchSize=1
   > ebean.debug.sql=true
   > ebean.debug.lazyload=false

   > datasource.default=mysql
   > datasource.mysql.username=root
   > datasource.mysql.password=root
   > datasource.mysql.databaseUrl=jdbc:mysql://localhost:3306/test
   > datasource.mysql.databaseDriver=com.mysql.jdbc.Driver

   > ```

   > (application-production.properties)

   > ```
   > action.config.basepath=./
   > entity.scanning.packages=test.entity.**
   > service.scanning.resource.names=test.service
   > service.slowtime.milliseconds=1000
   > #service.scanning.transaction=insert*,update*,delete*,save*

   > # generate DDL files
   > ebean.ddl.generate=false
   > # run ddl drops and recreates tables
   > ebean.ddl.run=false
   > ebean.uuidStoreAsBinary=true
   > ebean.databaseSequenceBatchSize=1
   > ebean.debug.sql=true
   > ebean.debug.lazyload=false

   > datasource.default=production
   > datasource.production.username=root
   > datasource.production.password=root
   > datasource.production.databaseUrl=jdbc:mysql://localhost:3306/test
   > datasource.production.databaseDriver=com.mysql.jdbc.Driver
   > ```
   
## **AppConfitUtil**属性读取

> AppConfigUtil帮助类用于读取属性文件中的配置

示例:

```
	String emailHost = AppConfigUtil.getProperty("email.host");
	
	String emailHost = AppConfigUtil.getProperty("email.host","defaultValue(127.0.0.1)");
```

注意： 配置读取规则为，首先读取当前profile对应的应用属性配置文件中的配置，如果找不到，再读取主应用属性配置文件。

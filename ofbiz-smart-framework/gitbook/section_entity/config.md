**OFBiz Smart**框架有且只有一个全局默认配置文件，位于**classpath**的根目录下，且名为**application.properties**. 有关实体引擎的配置就在该文件里。

```
#定义了要扫描的实体所在的包，多个包以逗号隔开
 entity.scanning.packages=org.huihoo.samples.petclinic.model.**,test.model.model.**
#定义了种子数据SQL文件，相对于classpath目录,多个文件以逗号隔开
seed.data.sql.file=seed_data.sql,seed_data2.sql
 
#########################################
#   ebean独有的配置
#########################################
# 是否生成ddl语句
ebean.ddl.generate=true
# 是否执行ddl语句
ebean.ddl.run=true
# 当主键定义为UUID时，该主键是否以binary格式保存
ebean.uuidStoreAsBinary=true
# 每批次获取的数据库序列的个数是多少
ebean.databaseSequenceBatchSize=1
# 是否开启SQL调试日志
ebean.debug.sql=true
######################################

#默认使用哪个数据源
datasource.default=h2
#h2数据源配置
datasource.h2.provider=
datasource.h2.username=sa
datasource.h2.password=
datasource.h2.databaseUrl=jdbc:h2:mem:tests
datasource.h2.databaseDriver=org.h2.Driver

#mysql数据源配置
datasource.mysql.provider=
datasource.mysql.username=root
datasource.mysql.password=root
datasource.mysql.databaseUrl=jdbc:mysql://localhost:3306/testdatasource.mysql.databaseDriver=com.mysql.jdbc.Driver

```

##注意：
> **datasource.mysql.provider**属性指定了数据源的提供实现，如果不指定，**OFBiz Smart**默认提供了一个基于[HikariCP](https://github.com/brettwooldridge/HikariCP)的实现。如果指定其它实现，参考[第三方数据源](../section_entity/thirdparty_ds.html)

> **entity.scanning.packages**属性必须指定，**Ebean**对实体基于**Java Agent**运行时动态加强这一特性，需要显示指定实体所在的包. 仅在开发模式时起作用。

> **seed.data.sql.file**属性指定了应用运行所必须的种子数据，仅支持**insert**语句,仅在开发模式时启用。
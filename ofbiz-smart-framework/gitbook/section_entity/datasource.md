**Delegator**支持多数据源，因此也支持数据源切换。

如下的数据源配置：

```
datasource.default=h2

datasource.h2.provider=org.huihoo.ofbiz.smart.entity.DefaultDataSourceProvider
datasource.h2.username=sa
datasource.h2.password=
datasource.h2.databaseUrl=jdbc:h2:mem:tests
datasource.h2.databaseDriver=org.h2.Driver

datasource.mysql.username=root
datasource.mysql.password=root
datasource.mysql.databaseUrl=jdbc:mysql://localhost:3306/test
datasource.mysql.databaseDriver=com.mysql.jdbc.Driver

```

从上面的配置来看，有两个数据源，一个数据源名为**h2**,一个数据源名为**mysql**。 默认使用**h2**。

如果**Delegator**未显示指定数据源，默认使用 **datasource.default**指定的数据源**h2**

如果需要显示指定数据源，使用**Delegator.useDataSource(String dsName)**来指定。

如下所示：

```
//使用默认数据源
Customer c1 = new Customer();
c1.setFirstname("test001");
delegator.save(c1);

//使用mysql数据源
Customer c2 = new Customer();
c2.setFirstname("c2");
delegator.useDataSource("mysql").save(c2);

```
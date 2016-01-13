**Delegator**默认的数据源提供者实现为[HikariCP](https://github.com/brettwooldridge/HikariCP),一个高性能，稳定的数据源实现。

如果需要整合其它数据源，参照以下步骤：

1. 实现**org.huihoo.ofbiz.smart.entity.DataSourceProvider**接口
2. 在配置文件中指定

示例：

```java
//整合C3P0数据源

//1. 实现类
public class C3P0DataSourceProvider implements DataSourceProvider {
  //prop为加载的全局应用配置
  //datasourceName为数据源的名称
  @Override
  public DataSource datasource(Properties prop, String datasourceName) {
    ComboPooledDataSource ds = new ComboPooledDataSource();
    ds.setJdbcUrl(prop.getProperty(""));//从配置文件中获取
    ds.setDriverClass(prop.getProperty(""));//从配置文件中获取
    ds.setUser(prop.getProperty(""));//从配置文件中获取
    ds.setPassword(prop.getProperty(""));//从配置文件中获取
    return ds;
  }
}

//2. 配置
datasource.h2.provider=provider.C3P0DataSourceProvider
datasource.h2.username=sa
datasource.h2.password=
datasource.h2.databaseUrl=jdbc:h2:mem:tests
datasource.h2.databaseDriver=org.h2.Driver

datasource.h2.provider=provider.C3P0DataSourceProvider
datasource.mysql.username=root
datasource.mysql.password=root
datasource.mysql.databaseUrl=jdbc:mysql://localhost:3306/test
datasource.mysql.databaseDriver=com.mysql.jdbc.Driver


```
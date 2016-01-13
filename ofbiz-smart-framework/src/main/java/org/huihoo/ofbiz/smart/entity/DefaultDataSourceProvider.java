package org.huihoo.ofbiz.smart.entity;


import com.zaxxer.hikari.HikariDataSource;
import org.huihoo.ofbiz.smart.base.C;

import javax.sql.DataSource;
import java.util.Properties;

public class DefaultDataSourceProvider implements DataSourceProvider {

  @Override
  public DataSource datasource(Properties prop, String datasourceName) {
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(prop.getProperty(C.CONFIG_DATASOURCE + "." + datasourceName + ".databaseUrl"));
    ds.setDriverClassName(prop.getProperty(C.CONFIG_DATASOURCE + "." + datasourceName + ".databaseDriver"));
    ds.setUsername(prop.getProperty(C.CONFIG_DATASOURCE + "." + datasourceName + ".username"));
    ds.setPassword(prop.getProperty(C.CONFIG_DATASOURCE + "." + datasourceName + ".password"));
    // TODO 其它配置加强
    return ds;
  }
}

package org.huihoo.ofbiz.smart.entity;


import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourceProvider {

  DataSource datasource(Properties prop, String datasourceName);

}

# 实体引擎

实体引擎定义了一个标准**Delegator**接口，
该接口定义了常见的数据访问操作； 支持多个数据源; 支持第三方数据源**Datasource**。


### 核心类图

![service](../section_entity/entity.png)

>Delegator 定义了各种与实体操作的相关API，并依赖 TxRunnable 和 TxCallable 接口，以支持事务操作。

>默认实现 [EbeanDelegator](ebean.md) 基于Ebean6.x 实现了 Delegator，并依赖 DataSourceProvider，以支持多数据源。

>DataSourceProvider 数据源提供者接口，定义了具体的数据源实现必须要实现的方法。

>DefaultDataSourceProvider 框架内置的默认数据源提供者实现。
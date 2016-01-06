**Delegator**的初始化很简单，直接**new**一个具体实现类即可。

> Delegator是一个比较重的对象，但它不是以单例模式设计的，所以要求应用方自行管理该对象，强烈建议应用中中仅存在唯一实例。

如下所示：

```
Delegator delegator = new EbeanDelegator();
```

初始化过程:

1. 在**classpath**根路径加载**application.peroperties**
2. 在开发模式下，扫描实体所在的包，动态增加实体的特性
3. 创建一个名为**EntityCache**的全局缓存，用于缓存实体
4. 初始化加载数据源，支持多数据源
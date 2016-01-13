**Delegator**支持事务，如果操作需要事务控制，通过**显示模式**和**回调模式**两种方式来执行。

### 显示模式

即显示指定开始事务，提交事务，回滚事务，结束事务。

如下所示：

```java
    try {
      delegator.beginTransaction();
      //在这里编写代码
      delegator.commitTransaction();
    } catch (Exception e) {
      delegator.rollback();
    } finally {
      delegator.endTransaction();
    }
    
```

### 回调模式

即通过实现对应回调接口的方式来执行

如下所示：

```java
    delegator.executeWithInTx(new TxCallable() {
      @Override
      public Object call() {
        //在这里编写代码，适用于有返回值的情况
        return null;
      }
    });    
    
    delegator.executeWithInTx(new TxRunnable() {
      @Override
      public void run() {
        //在这里编写代码，适用于没有返回值的情况。
      }
    });
    
```
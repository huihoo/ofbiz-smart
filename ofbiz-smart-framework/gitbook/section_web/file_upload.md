# FileUploadHandler

框架定义了一个文件上传接口，可以实现该接口，满足实际的文件上传需求。

接口定义如下：

```java
  /**
   * 处理文件上传
   * 
   * @param fieldName   文件域的名称
   * @param fileName    文件的原始名称
   * @param contentType 文件的类型
   * @param inputStream 文件的输入流
   * @param webCtx      WebApp上下文
   * @return Map
   */
  Map<String,Object> handle(String fieldName,String fileName,String contentType,
                            InputStream inputStream,Map<String,Object> webCtx);

```

# DefaultFileUploadHandler

框架内置的默认**FileUploadHandler**实现。

属性配置

|名称   |描述
|:----:|:-----|
|file.upload.save.relative.path|上传文件的保存路径,相对于webapp根路径，默认为 /upload 
|file.upload.allow.content.type|允许上传的文件类型，多个以逗号隔开，默认为image/x-png,image/pjpeg,image/gif,image/jpeg,image/png
|file.upload.folder.date.formatter|保存文件目录的生成格式（基于当前时间），默认为 yyyy/MM/dd 

# 上传成功后返回**Map**的主要属性

|key      |value type  |描述
|:----  |:-----|:-------|
|fieldName|String|文件的相对路径
|myfile_file_field_name|String|文件域的名称
|fieldName_file_saved_file|File|成功保存的文件
|fieldName_file_new_name|String|文件的新名称
|fieldName_file_origal_name|String|文件的原始名称
|fieldName_file_content_type|String|文件的类型
|fieldName_file_size|Long|文件的大小(单位字节)

比如如下的**Map**结构:

```java
 {
  myfile=/upload/2016/01/10/a9f206a9a1504727bc9429c423feea3e.png
  ,myfile_file_field_name=myfile
  ,myfile_file_saved_file=java.io.File@1234
  ,myfile_file_new_name=a9f206a9a1504727bc9429c423feea3e.png
  ,myfile_file_origal_name=test.png
  ,myfile_file_content_type=image/png
  ,myfile_file_size=10241024
 }
```

# 自定义上传

1. 实现上面提到的**FileUploadHandler**接口

2. 在**application.properties**中进行配置
   ```
   file.upload.handler=pgkname.YourFileUploader
   ```
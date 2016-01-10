# 自定义界面

## 实现View接口

```java
public class MyExcelView implements View {
  @Override
  public String getContentType() {
    return "application/vnd.ms-excel";
  }
  
  @Override
  public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response){
    //your code here.
  }
}

public class MyPDFView implements View {
  @Override
  public String getContentType() {
    return "application/pdf";
  }
  
  @Override
  public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response){
    //your code here.
  }
}
```

## **application.properties**中配置

```java
#格式为view-type名称#实现类的全路径名称
webapp.supported.views=myexcel#pkgname.MyExcelView,mypdf#pkgname.MyPDFView
```

## Action配置

```xml
 <action uri="/my_excel_export">
   <response view-type="myexcel"/>
 </action>
 
 <action uri="/my_pdf_export">
   <response view-type="mypdf"/>
 </action>
```

package test.util;


import org.huihoo.ofbiz.smart.base.util.I18NUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18NUtilTest {
  private final static String TAG = I18NUtilTest.class.getName();

  @Test
  public void testAllInOne() {
    Locale locale = new Locale("zh", "CN");
    ResourceBundle resourceBundle = I18NUtil.getResourceBundle("i18n_ui", locale);
    Log.d("resourceBundle > " + resourceBundle, TAG);
    Assert.assertNotNull(resourceBundle);

    String btnOk = resourceBundle.getString("btnOk");
    Assert.assertEquals("确定", btnOk);

    resourceBundle = I18NUtil.getResourceBundle("i18n_ui", locale);
    Assert.assertNotNull(resourceBundle);

    String btnCancle = resourceBundle.getString("btnCancle");
    Assert.assertEquals("取消", btnCancle);

    resourceBundle = I18NUtil.getResourceBundle("ui", locale);
    Assert.assertNotNull(resourceBundle);

    btnOk = resourceBundle.getString("btnOk");
    Assert.assertEquals("确定", btnOk);

    resourceBundle = I18NUtil.getResourceBundle("ui", locale);
    Assert.assertNotNull(resourceBundle);

    btnCancle = resourceBundle.getString("btnCancle");
    Assert.assertEquals("取消", btnCancle);

    locale = new Locale("en", "US");
    resourceBundle = I18NUtil.getResourceBundle("i18n_ui", locale);
    Assert.assertNotNull(resourceBundle);

    btnOk = resourceBundle.getString("btnOk");
    Assert.assertEquals("OK", btnOk);

    btnCancle = resourceBundle.getString("btnCancle");
    Assert.assertEquals("Cancle", btnCancle);

    resourceBundle = I18NUtil.getResourceBundle("ui", locale);
    Assert.assertNotNull(resourceBundle);

    btnOk = resourceBundle.getString("btnOk");
    Assert.assertEquals("OK", btnOk);

    btnCancle = resourceBundle.getString("btnCancle");
    Assert.assertEquals("Cancle", btnCancle);
  }
}

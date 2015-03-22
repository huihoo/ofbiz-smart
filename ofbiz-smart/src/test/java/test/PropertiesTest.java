package test;

import java.util.Locale;
import java.util.ResourceBundle;




import org.huihoo.ofbiz.smart.base.utils.PropertiesUtils;
import org.junit.Assert;
import org.junit.Test;


public class PropertiesTest {
  @Test
  public void testIt() {
    Locale locale = new Locale("zh");
    ResourceBundle resourceBundle = PropertiesUtils.getResourceBundle("/test/AccountingUiLabels.xml", locale);
    Assert.assertNotNull(resourceBundle);
    String a = resourceBundle.getString("AccountingAHCElectronicCheck");
    Assert.assertEquals("电子资金转账账户：金融机构委员会/电子支票", a);
    
    locale = new Locale("en");
    resourceBundle = PropertiesUtils.getResourceBundle("/test/AccountingUiLabels.xml", locale);
    Assert.assertNotNull(resourceBundle);
    a = resourceBundle.getString("AccountingAHCElectronicCheck");
    Assert.assertEquals("EFT Account: AHC/Electronic Check", a);
  }
}

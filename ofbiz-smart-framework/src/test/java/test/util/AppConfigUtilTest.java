package test.util;

import org.huihoo.ofbiz.smart.base.util.AppConfigUtil;
import org.junit.Assert;
import org.junit.Test;

public class AppConfigUtilTest {
  
  @Test
  public void testAllInOne() {
    String acb = AppConfigUtil.getProperty("action.config.basepath");
    Assert.assertEquals("./", acb);
    
    String currentProfile = AppConfigUtil.getProperty("profile");
    Assert.assertEquals("test", currentProfile);
    
    String emailHost = AppConfigUtil.getProperty("email.host");
    Assert.assertEquals("mail@qq.com", emailHost);
    
    emailHost = AppConfigUtil.getPropertyByProfile("test","email.host");
    Assert.assertEquals("mail@qq.com", emailHost);
    
    emailHost = AppConfigUtil.getPropertyByProfile("develop","email.host");
    Assert.assertEquals("mail@qq.com", emailHost);
    
    emailHost = AppConfigUtil.getPropertyByProfile("production","email.host");
    Assert.assertEquals("mail@163.com", emailHost);
  }
  
}

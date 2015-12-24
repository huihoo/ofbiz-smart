package test.webapp;

import java.io.IOException;
import java.util.Set;

import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.webapp.ActionModel;
import org.huihoo.ofbiz.smart.webapp.ActionModelXmlConfigLoader;
import org.junit.Assert;
import org.junit.Test;


public class WebAppTest {
  private final static String TAG = WebAppTest.class.getName();
  
  @Test
  public void testLoadConfig() throws IOException {
    Log.d("Start testing....", TAG);
    
    String path = FlexibleLocation.resolveLocation("./").getPath();
    Log.d("Path >" + path, TAG);
    ActionModelXmlConfigLoader.loadXml(path);
    Set<ActionModel> actionModels = ActionModelXmlConfigLoader.getAllActionModel();
    Log.d("actionModels > " +actionModels, TAG);
    Assert.assertEquals(true, actionModels.size() > 0);
  }
}

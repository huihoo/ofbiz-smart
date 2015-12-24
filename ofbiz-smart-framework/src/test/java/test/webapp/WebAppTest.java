package test.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.webapp.ActionModel;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Action;
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
    List<ActionModel> actionModels = new ArrayList<>(); 
    ActionModelXmlConfigLoader.me().loadXml(path,actionModels);
    Log.d("actionModels > " +actionModels, TAG);
    Assert.assertEquals(true, actionModels.size() > 0);
    
    Action action = shouldHasAction("/order/create", actionModels);
    Assert.assertNotNull(action);
    Assert.assertEquals(true, "byConfig".equals(action.processType));
    Assert.assertEquals(true, action.serviceCallList.size() == 1);
    
    Assert.assertEquals(true, action.serviceCallList.get(0).serviceName.equals("createOrder"));
    Assert.assertEquals(true, "json".equals(action.response.viewType));
    
    action = shouldHasAction("/product/detail", actionModels);
    Assert.assertNotNull(action);
    
    action = shouldHasAction("/news/**", actionModels);
    Assert.assertNotNull(action);
    
    action = shouldHasAction("/customer/detail", actionModels);
    Assert.assertNotNull(action);
  }
  
  
  private Action shouldHasAction(String actionUri,List<ActionModel> actionModels) {
    for (ActionModel actionModel : actionModels) {
      List<Action> actions = actionModel.actionList;
      for (Action action : actions) {
        if (actionUri.equals(action.uri)) {
          return action;
        }
      }
    }
    return null;
  }
}

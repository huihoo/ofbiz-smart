package test.util;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.junit.Assert;
import org.junit.Test;

public class ClearXssTest {
  private final static String TAG = ClearXssTest.class.getName();

  @Test
  public void testAllInOne() {
    String chineseText = "我是好人";
    String clearedText =  CommUtil.stripXSS(chineseText);
    Log.i("cleared text : " + clearedText, TAG);
    Assert.assertEquals(chineseText, clearedText);
  }
}

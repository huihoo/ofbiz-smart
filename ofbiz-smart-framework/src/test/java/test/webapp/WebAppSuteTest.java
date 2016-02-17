package test.webapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({WebAppTest.class,SelectOptionTagTest.class,WebWithJettyTest.class})
public class WebAppSuteTest {
  public WebAppSuteTest() {}
}

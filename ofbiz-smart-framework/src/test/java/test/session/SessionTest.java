package test.session;

import org.huihoo.ofbiz.smart.session.ExpiringSession;
import org.huihoo.ofbiz.smart.session.MapSessionRepository;
import org.huihoo.ofbiz.smart.session.SessionRepository;
import org.junit.Assert;
import org.junit.Test;

import test.entity.BaseTestCase;

public class SessionTest extends BaseTestCase {
  private final static String TAG = SessionTest.class.getName();

  @Test
  public void testAllInOne() throws InterruptedException {
    SessionRepository<ExpiringSession> repository = new MapSessionRepository();
    ExpiringSession session = repository.createSession();
    session.setMaxInactiveIntervalInSeconds(2);
    session.setAttribute("user", "user");
    
    repository.save(session);
    
    ExpiringSession fromSession = repository.getSession(session.getId());
    Assert.assertEquals("user", fromSession.getAttribute("user"));
    
    Thread.sleep(3000);
    
    Assert.assertNull(fromSession.getAttribute("user"));
  }

}

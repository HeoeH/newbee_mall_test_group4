package ltd.newbee.mall.controller.mall;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.common.Constants;
import org.junit.Before;
import org.junit.Test;
import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;

public class LogoutTest_WhiteBox {
    private HttpSession httpSession = EasyMock.createMock(HttpSession.class);

    @Before
    public void setUp(){
        EasyMock.reset(httpSession);

        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        EasyMock.expectLastCall().once();

        EasyMock.replay(httpSession);
    }

    @Test
    public void testLogout_WhiteBox(){
        PersonalController controller = new PersonalController();

        String result = controller.logout(httpSession);

        EasyMock.verify(httpSession);
        assertEquals(result, "mall/login");
    }
}

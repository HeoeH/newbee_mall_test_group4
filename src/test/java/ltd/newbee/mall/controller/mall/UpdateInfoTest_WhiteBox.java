package ltd.newbee.mall.controller.mall;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.service.NewBeeMallUserService;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.easymock.EasyMock;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UpdateInfoTest_WhiteBox {
    private HttpSession httpSession = EasyMock.createMock(HttpSession.class);
    private NewBeeMallUserService newBeeMallUserService = EasyMock.createMock(NewBeeMallUserService.class);

    private MallUser mallUser;
    private String expectedResult;

    public UpdateInfoTest_WhiteBox(MallUser mallUser, String expectedResult) {
        this.mallUser = mallUser;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null , "修改失败"},
                {new MallUser() , ResultGenerator.genSuccessResult().getMessage()},
        });
    }

    @Before
    public void setup() {
        EasyMock.reset(httpSession, newBeeMallUserService);

        if(mallUser == null)
            EasyMock.expect(newBeeMallUserService.updateUserInfo(mallUser,httpSession)).andReturn(null).once();
        else
            EasyMock.expect(newBeeMallUserService.updateUserInfo(mallUser,httpSession)).andReturn(new NewBeeMallUserVO()).once();

        EasyMock.replay(httpSession, newBeeMallUserService);
    }

    @Test
    public void testLogin_WhiteBox() {
        // 创建控制器实例
        PersonalController controller = new PersonalController();
        controller.setNewBeeMallUserService(newBeeMallUserService);

        // 调用方法
        Result result = controller.updateInfo(mallUser, httpSession);

        // 验证结果
        EasyMock.verify(httpSession, newBeeMallUserService);
        assertEquals(expectedResult, result.getMessage());
    }
}

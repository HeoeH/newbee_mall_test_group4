package ltd.newbee.mall.controller.mall;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.service.NewBeeMallUserService;
import ltd.newbee.mall.util.MD5Util;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.easymock.EasyMock;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LoginTest_WhiteBox {

    private HttpSession httpSession = EasyMock.createMock(HttpSession.class);
    private NewBeeMallUserService newBeeMallUserService = EasyMock.createMock(NewBeeMallUserService.class);

    private String loginName;
    private String verifyCode;
    private String password;
    private String kaptchaCode;
    private String expectedResult;

    public LoginTest_WhiteBox(String loginName, String verifyCode, String password, String kaptchaCode, String expectedResult) {
        this.loginName = loginName;
        this.verifyCode = verifyCode;
        this.password = password;
        this.kaptchaCode = kaptchaCode;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                //测试用例1:登录成功
                { "testUser", "validCode", "pass" , "validcode" , ResultGenerator.genSuccessResult().getMessage()},
                //测试用例2:用户名为空
                { "", "validCode", "pass2" , "validcode" , ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult()).getMessage()},
                //测试用例3:密码为空
                { "testUser", "validCode", "" , "validcode" , ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult()).getMessage()},
                //测试用例4:验证码错误
                { "testUser", "inValidCode", "pass" , "validcode" , ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult()).getMessage()},
                //测试用例5:密码错误
                { "testUser", "validCode", "fail" , "validcode" , ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_ERROR.getResult()).getMessage()},
                //测试用例6:验证码为空
                { "testUser", "", "pass" , "validcode" , ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult()).getMessage()},
                //测试用例7:kaptchaCode为空
                { "testUser", "validCode", "pass" , "" , ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult()).getMessage()}
        });
    }

    @Before
    public void setup() {
        EasyMock.reset(httpSession, newBeeMallUserService);
        EasyMock.expect(httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY)).andReturn(kaptchaCode).anyTimes();
        /**
         * 用于模拟用户名密码正确与否的情况
         */
        if (Objects.equals(password, "pass"))
            EasyMock.expect(newBeeMallUserService.login(loginName, MD5Util.MD5Encode(password, Constants.UTF_ENCODING), httpSession)).andReturn(ServiceResultEnum.SUCCESS.getResult()).anyTimes();
        else
            EasyMock.expect(newBeeMallUserService.login(loginName, MD5Util.MD5Encode(password, Constants.UTF_ENCODING), httpSession)).andReturn(ServiceResultEnum.LOGIN_ERROR.getResult()).anyTimes();

        httpSession.setAttribute(Constants.MALL_VERIFY_CODE_KEY,null);
        EasyMock.expectLastCall().anyTimes();
        httpSession.removeAttribute(Constants.MALL_VERIFY_CODE_KEY);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(httpSession, newBeeMallUserService);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(EasyMock.createMock(HttpServletRequest.class)));
    }

    @Test
    public void testLogin_WhiteBox() {
        // 创建控制器实例
        PersonalController controller = new PersonalController();
        controller.setNewBeeMallUserService(newBeeMallUserService);

        // 调用方法
        Result result = controller.login(loginName, verifyCode, password, httpSession);

        // 验证结果
        EasyMock.verify(httpSession, newBeeMallUserService);
        assertEquals(expectedResult, result.getMessage());
    }
}

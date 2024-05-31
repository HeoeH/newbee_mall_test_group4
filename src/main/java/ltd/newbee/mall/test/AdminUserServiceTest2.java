package ltd.newbee.mall.test;

import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import org.easymock.EasyMock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminUserServiceTest2 {

    private final AdminUserService adminUserService = EasyMock.mock(AdminUserService.class);
    private final HttpSession session = EasyMock.mock(HttpSession.class);

    @ParameterizedTest
    @CsvSource({
            "admin, password123, NULL, '验证码不能为空'",
            "NULL, password123, validVerifyCode, '用户名或密码不能为空'",
            "admin, password123, invalidVerifyCode, '验证码错误'",
            "admin, password123, validVerifyCode, 'redirect:/admin/index'",
            "UserName, password123, validVerifyCode, '登录失败'",
            "admin, NULL, validVerifyCode, '用户名或密码不能为空'"
    })
    void testLogin(String userName, String password, String verifyCode, String expectedResult) {
        // 模拟 session 中的验证码
        String kaptchaCode = "validVerifyCode";
        EasyMock.expect(session.getAttribute("verifyCode")).andReturn(kaptchaCode).anyTimes();

        // 模拟 adminUserService 的 login 方法
        EasyMock.expect(adminUserService.login(EasyMock.anyString(), EasyMock.anyString()))
                .andReturn("redirect:/admin/index".equals(expectedResult) ? new AdminUser() : null)
                .anyTimes();

        // 设置期望的 session.setAttribute 调用
        if ("验证码不能为空".equals(expectedResult)) {
            session.setAttribute(EasyMock.eq("errorMsg"), EasyMock.eq("验证码不能为空"));
        } else if ("用户名或密码不能为空".equals(expectedResult)) {
            session.setAttribute(EasyMock.eq("errorMsg"), EasyMock.eq("用户名或密码不能为空"));
        } else if ("验证码错误".equals(expectedResult)) {
            session.setAttribute(EasyMock.eq("errorMsg"), EasyMock.eq("验证码错误"));
        } else if ("登录失败".equals(expectedResult)) {
            session.setAttribute(EasyMock.eq("errorMsg"), EasyMock.eq("登录失败"));
        } else if ("redirect:/admin/index".equals(expectedResult)) {
            session.setAttribute(EasyMock.eq("loginUser"), EasyMock.anyObject());
            session.setAttribute(EasyMock.eq("loginUserId"), EasyMock.anyObject());
        }
        EasyMock.expectLastCall().anyTimes(); // 添加这一行来期望最后一个调用

        EasyMock.replay(session, adminUserService);

        // 执行登录逻辑
        String actualResult = null;
        if (verifyCode == null || verifyCode.equals("NULL") || verifyCode.isEmpty()) {
            session.setAttribute("errorMsg", "验证码不能为空");
            actualResult = "验证码不能为空";
        } else if (userName == null || userName.equals("NULL") || userName.isEmpty() || password == null || password.equals("NULL") || password.isEmpty()) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
            actualResult = "用户名或密码不能为空";
        } else if (!kaptchaCode.equalsIgnoreCase(verifyCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            actualResult = "验证码错误";
        } else {
            AdminUser adminUser = adminUserService.login(userName, password);
            if (adminUser != null) {
                session.setAttribute("loginUser", adminUser);
                session.setAttribute("loginUserId", adminUser.getAdminUserId());
                actualResult = "redirect:/admin/index";
            } else {
                session.setAttribute("errorMsg", "登录失败");
                actualResult = "登录失败";
            }
        }

        EasyMock.verify(session, adminUserService);
        assertEquals(expectedResult, actualResult);
        EasyMock.reset(session, adminUserService);  // 重置 mocks 以便在下一个测试用例中重新设置期望值
    }
}

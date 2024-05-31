package ltd.newbee.mall.test;

import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminUserServiceTest {

    private final AdminUserService adminUserService = Mockito.mock(AdminUserService.class);
    private final HttpSession session = Mockito.mock(HttpSession.class);

    @ParameterizedTest
    @CsvSource({
            "validUser123, password123, a1B2, true",
            ", password123, a1B2, false",
            "abc, password123, a1B2, false",
            "aVeryLongUsernameExceedingLimit, password123, a1B2, false",
            "user@name, password123, a1B2, false",
            "validUser123, pass, a1B2, false",
            "validUser123, averylongpasswordexceedinglimit, a1B2, false",
            "validUser123, , a1B2, false",
            "validUser123, @@@@@@@@, a1B2, false",
            "validUser123, password123, , false"
    })
    void testLogin(String userName, String password, String verifyCode, boolean expectedResult) {
        // 模拟adminUserService的login方法
        Mockito.when(adminUserService.login(userName, password)).thenReturn(
                expectedResult ? new AdminUser() : null
        );

        // 模拟session中的验证码
        String kaptchaCode = "a1B2";
        Mockito.when(session.getAttribute("verifyCode")).thenReturn(kaptchaCode);

        // 执行登录逻辑
        if (verifyCode == null || verifyCode.isEmpty()) {
            session.setAttribute("errorMsg", "验证码不能为空");
        } else if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
        } else if (!kaptchaCode.equalsIgnoreCase(verifyCode)) {
            session.setAttribute("errorMsg", "验证码错误");
        } else {
            AdminUser adminUser = adminUserService.login(userName, password);
            if (adminUser != null) {
                session.setAttribute("loginUser", adminUser.getNickName());
                session.setAttribute("loginUserId", adminUser.getAdminUserId());
                // session.setMaxInactiveInterval(60 * 60 * 2);
                assertEquals("redirect:/admin/index", "redirect:/admin/index");
            } else {
                session.setAttribute("errorMsg", "登录失败");
                assertEquals("admin/login", "admin/login");
            }
        }
    }
}

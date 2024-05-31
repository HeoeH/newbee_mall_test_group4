package ltd.newbee.mall.test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.controller.admin.AdminController;
import ltd.newbee.mall.service.AdminUserService;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminControllerTest {

    private AdminUserService adminUserService;
    private HttpServletRequest request;
    private HttpSession session;

    private AdminController adminController;

    @BeforeEach
    void setUp() {
        adminUserService = EasyMock.mock(AdminUserService.class);
        request = EasyMock.mock(HttpServletRequest.class);
        session = EasyMock.mock(HttpSession.class);
        adminController = new AdminController();
        adminController.adminUserService = adminUserService;
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', '参数不能为空'",
            "'', 'newPassword123', '参数不能为空'",
            "'originalPassword123', '', '参数不能为空'",
            "'originalPassword123', 'newPassword123', '修改失败'",
            "'wrongOriginalPassword', 'newPassword123', '修改失败'",
            "'OriginalPassword', 'newPassword123', 'success'"
    })
    void testPasswordUpdate(String originalPassword, String newPassword, String expectedResult) {
        Integer loginUserId = 1;

        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(session.getAttribute("loginUserId")).andReturn(loginUserId).anyTimes();

        if ("success".equals(expectedResult)) {
            EasyMock.expect(adminUserService.updatePassword(loginUserId, originalPassword, newPassword)).andReturn(true);
            session.removeAttribute("loginUserId");
            session.removeAttribute("loginUser");
            session.removeAttribute("errorMsg");
        } else if ("修改失败".equals(expectedResult)) {
            EasyMock.expect(adminUserService.updatePassword(loginUserId, originalPassword, newPassword)).andReturn(false);
        }

        EasyMock.replay(request, session, adminUserService);

        String actualResult = adminController.passwordUpdate(request, originalPassword, newPassword);

        assertEquals(expectedResult, actualResult);

        EasyMock.verify(request, session, adminUserService);
    }
}

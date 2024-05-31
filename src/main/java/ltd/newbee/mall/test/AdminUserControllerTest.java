package ltd.newbee.mall.test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.admin.AdminController;
import ltd.newbee.mall.service.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminController adminController;

    @Mock
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(request.getSession()).thenReturn(session);
    }

    @ParameterizedTest
    @CsvSource({
            "abc123, correctPassword, SUCCESS",
            "1234, correctPassword, 修改失败",
            "a1b2c3d4e5f6g7h8i9j0, correctPassword, SUCCESS",
            ", correctPassword, 参数不能为空",
            "@@@@@@@@, correctPassword, 修改失败",
            "abc123, , 参数不能为空",
            "abc123, wrongPassword, 修改失败"
    })
    void testPasswordUpdate(String originalPassword, String newPassword, String expectedResult) {
        when(session.getAttribute("loginUserId")).thenReturn(1);

        if ("SUCCESS".equals(expectedResult)) {
            when(adminUserService.updatePassword(anyInt(), eq(originalPassword), eq(newPassword))).thenReturn(true);
        } else {
            when(adminUserService.updatePassword(anyInt(), eq(originalPassword), eq(newPassword))).thenReturn(false);
        }

        String result = adminController.passwordUpdate(request,originalPassword, newPassword);

        if ("SUCCESS".equals(expectedResult)) {
            verify(session).removeAttribute("loginUserId");
            verify(session).removeAttribute("loginUser");
            verify(session).removeAttribute("errorMsg");
            assertEquals(ServiceResultEnum.SUCCESS.getResult(), result);
        } else {
            assertEquals(expectedResult, result);
        }
    }

    @Test
    public void testPasswordUpdateSuccess() {
        when(session.getAttribute("loginUserId")).thenReturn(1);

        String originalPassword = "abc123";
        String newPassword = "correctPassword";

        when(adminUserService.updatePassword(anyInt(), eq(originalPassword), eq(newPassword))).thenReturn(true);

        String result = adminController.passwordUpdate(request,originalPassword, newPassword);

        verify(session).removeAttribute("loginUserId");
        verify(session).removeAttribute("loginUser");
        verify(session).removeAttribute("errorMsg");

        assertEquals(ServiceResultEnum.SUCCESS.getResult(), result);
    }

    @Test
    public void testPasswordUpdateFailure() {
        when(session.getAttribute("loginUserId")).thenReturn(1);

        String originalPassword = "abc123";
        String newPassword = "wrongPassword";

        when(adminUserService.updatePassword(anyInt(), eq(originalPassword), eq(newPassword))).thenReturn(false);

        String result = adminController.passwordUpdate(request,originalPassword, newPassword);

        assertEquals("修改失败", result);
    }
}

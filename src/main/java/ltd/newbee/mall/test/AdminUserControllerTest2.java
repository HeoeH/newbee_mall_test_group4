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
import static org.mockito.Mockito.when;

public class AdminUserControllerTest2 {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminController adminController;

    @Mock
    private HttpServletRequest request;

    @Mock
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
            "validUser123, 用户123, SUCCESS",
            ", 用户123, 参数不能为空",
            "abc, 用户123, SUCCESS",
            "aVeryLongUsernameExceedingLimit, 用户123, SUCCESS",
            "user@name, 用户123, SUCCESS",
            "a1_b, 用户123, SUCCESS",
            "a1_b2_c3_d4_e5_f, 用户123, SUCCESS",
            "validUser123, , 参数不能为空",
            "validUser123, a, SUCCESS",
            "validUser123, 这是一个很长的昵称超过了十八位, SUCCESS",
            "validUser123, 用!户123, SUCCESS",
            "validUser123, 汉字, SUCCESS",
            "validUser123, 用户123, SUCCESS"
    })
    void testNameUpdate(String loginUserName, String nickName, String expectedResult) {
        when(session.getAttribute("loginUserId")).thenReturn(1);

        if ("SUCCESS".equals(expectedResult)) {
            when(adminUserService.updateName(anyInt(), eq(loginUserName), eq(nickName))).thenReturn(true);
        } else {
            when(adminUserService.updateName(anyInt(), eq(loginUserName), eq(nickName))).thenReturn(false);
        }

        String result = adminController.nameUpdate(request, response, loginUserName, nickName);

        if ("SUCCESS".equals(expectedResult)) {
            assertEquals(ServiceResultEnum.SUCCESS.getResult(), result);
        } else {
            assertEquals(expectedResult, result);
        }
    }

    @Test
    public void testNameUpdateSuccess() {
        when(session.getAttribute("loginUserId")).thenReturn(1);

        String loginUserName = "validUser123";
        String nickName = "用户123";

        when(adminUserService.updateName(anyInt(), eq(loginUserName), eq(nickName))).thenReturn(true);

        String result = adminController.nameUpdate(request, response, loginUserName, nickName);

        assertEquals(ServiceResultEnum.SUCCESS.getResult(), result);
    }

    @Test
    public void testNameUpdateFailure() {
        when(session.getAttribute("loginUserId")).thenReturn(1);

        String loginUserName = "validUser123";
        String nickName = "用户123";

        when(adminUserService.updateName(anyInt(), eq(loginUserName), eq(nickName))).thenReturn(false);

        String result = adminController.nameUpdate(request, response, loginUserName, nickName);

        assertEquals("修改失败", result);
    }
}

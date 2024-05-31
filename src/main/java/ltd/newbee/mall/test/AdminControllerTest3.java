package ltd.newbee.mall.test;

import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.controller.admin.AdminController;
import ltd.newbee.mall.service.AdminUserService;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest3 {

    private MockMvc mockMvc;
    private AdminUserService adminUserService;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        adminUserService = EasyMock.mock(AdminUserService.class);
        session = EasyMock.mock(HttpSession.class);
        AdminController adminController = new AdminController();
        adminController.adminUserService = adminUserService;
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @ParameterizedTest
    @CsvSource({
            "NULL, valid, '参数不能为空'",
            "valid, NULL, '参数不能为空'",
            "valid, valid, '修改失败'",
            "valid, valid, 'success'"
    })
    void testNameUpdate(String loginUserName, String nickName, String expectedResult) throws Exception {
        // 模拟 HttpServletRequest 和 HttpSession 的行为
        if (!"参数不能为空".equals(expectedResult)) {
            EasyMock.expect(session.getAttribute("loginUserId")).andReturn(1).anyTimes();
        }

        if ("修改失败".equals(expectedResult)) {
            EasyMock.expect(adminUserService.updateName(1, loginUserName, nickName)).andReturn(false);
        } else if ("success".equals(expectedResult)) {
            EasyMock.expect(adminUserService.updateName(1, loginUserName, nickName)).andReturn(true);
        }

        EasyMock.replay(session, adminUserService);

        mockMvc.perform(post("/admin/profile/name")
                        .param("loginUserName", "NULL".equals(loginUserName) ? "" : loginUserName)
                        .param("nickName", "NULL".equals(nickName) ? "" : nickName)
                        .sessionAttr("loginUserId", 1))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult));

        EasyMock.verify(session, adminUserService);
        EasyMock.reset(session, adminUserService);
    }
}

package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.controller.admin.NewBeeMallGoodsIndexConfigController;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallIndexConfigService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class NewBeeMallGoodsIndexConfigControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private NewBeeMallIndexConfigService newBeeMallIndexConfigService;

    @Autowired(required = false)
    @InjectMocks
    private NewBeeMallGoodsIndexConfigController newBeeMallGoodsIndexConfigController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(newBeeMallGoodsIndexConfigController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvSource({"1, 10, 3, 200,'SUCCESS'", "'', 10, 4, 500,'参数异常！'", "'', 5,5, 500,'参数异常！'", "1, 5, 10, 500,'参数异常！'"})
    public void list(String page, String limit, String configType ,int expectedStatusCode,String expectedMessage) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("limit", limit);
        params.put("configType", configType);


        if (expectedStatusCode == 200) {
            PageQueryUtil pageUtil = new PageQueryUtil(params);
            List<IndexConfig> mockConfigs = Arrays.asList(
                    new IndexConfig(
                            1L,
                            "Config 1",
                            (byte) 1,
                            1L,
                            "redirect_url_1",
                            1,
                            (byte) 0,
                            new Date(),
                            1,
                            new Date(),
                            1
                    ),
                    new IndexConfig(
                            2L,
                            "Config 2",
                            (byte) 2,
                            2L,
                            "redirect_url_2",
                            2,
                            (byte) 0,
                            new Date(),
                            2,
                            new Date(),
                            2
                    )
            );
            PageResult<IndexConfig> pageResult = new PageResult<>(mockConfigs, 20, 10, 1);
            given(newBeeMallIndexConfigService.getConfigsPage(pageUtil)).willReturn(pageResult);
        }
        // 模拟请求并验证返回结果
        mockMvc.perform(get("/admin/indexConfigs/list")
                        .param("page", page)
                        .param("limit", limit)
                        .param("configType", configType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(expectedStatusCode))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }


}
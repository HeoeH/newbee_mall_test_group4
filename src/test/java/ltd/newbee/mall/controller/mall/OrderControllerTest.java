package ltd.newbee.mall.controller.mall;

import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.exception.NewBeeMallException;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private NewBeeMallOrderService newBeeMallOrderService;

    @MockBean
    private NewBeeMallShoppingCartService newBeeMallShoppingCartService;

    @Autowired(required = false)
    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvSource({
            "'Zhejiang', false, 302, '/orders/order123'",
            "'', false, 500, '地址不能为空！'",
            "'Zhejiang', true, 500, '购物车数据异常！'"
    })
    void saveOrder(String address, Boolean isShoppingCartItemEmpty, int expectedStatusCode, String expectedResult) throws Exception {
        Long couponUserId = 1L;
        HttpSession session = mock(HttpSession.class);
        NewBeeMallUserVO userVO = new NewBeeMallUserVO();
        userVO.setAddress(address);

        when(session.getAttribute(Constants.MALL_USER_SESSION_KEY)).thenReturn(userVO);

        if (!isShoppingCartItemEmpty) {
            List<NewBeeMallShoppingCartItemVO> shoppingCartItems = Collections.singletonList(new NewBeeMallShoppingCartItemVO());
            when(newBeeMallShoppingCartService.getMyShoppingCartItems(userVO.getUserId())).thenReturn(shoppingCartItems);
            when(newBeeMallOrderService.saveOrder(userVO, couponUserId, shoppingCartItems)).thenReturn("order123");
        } else {
            when(newBeeMallShoppingCartService.getMyShoppingCartItems(userVO.getUserId())).thenReturn(Collections.emptyList());
        }

        try{
            mockMvc.perform(get("/saveOrder")
                            .param("couponUserId", String.valueOf(couponUserId))
                            .sessionAttr(Constants.MALL_USER_SESSION_KEY, userVO)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(expectedStatusCode))
                    .andExpect(redirectedUrl(expectedResult));
        }catch (Exception e){
            assertTrue(e.getCause() instanceof NewBeeMallException);
            assertEquals(expectedResult, e.getCause().getMessage());
        }

    }
}

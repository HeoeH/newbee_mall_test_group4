package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class ShoppingCartControllerTest {

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    @MockBean
    private NewBeeMallShoppingCartService newBeeMallShoppingCartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @ParameterizedTest
    @CsvSource({
            "10901, 1, 1",
            "10902, 1.5, 1",
            "10803, 1c, 0",
            "10234, 0, 0",
            "10435, 0.8, 0",
            ", 6, 0",
            ", 3.8, 0",
            "1256a, 5, 0",
            "2215b, 3.6, 0"
    })
    void testSaveNewBeeMallShoppingCartItem(String itemIds, String itemCnts, Integer expectedResult) {
        Long itemId;
        Double itemCnt;
        NewBeeMallShoppingCartItem item = new NewBeeMallShoppingCartItem();
        try {
            itemId = Long.parseLong(itemIds);
            itemCnt = Double.parseDouble(itemCnts);
            item.setGoodsId(itemId);
            item.setGoodsCount(itemCnt.intValue());
        } catch (NumberFormatException nfe) {
            assertEquals(expectedResult, 0);
        } catch (NullPointerException npe) {
            assertEquals(expectedResult, 0);
        }

        MockHttpSession mockHttpSession = new MockHttpSession();
        NewBeeMallUserVO user = new NewBeeMallUserVO();
        user.setUserId(18L);
        mockHttpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, user);

        if(expectedResult == 1){
            when(newBeeMallShoppingCartService.saveNewBeeMallCartItem(item)).thenReturn(ServiceResultEnum.SUCCESS.getResult());
        }else if (expectedResult == 0){
            when(newBeeMallShoppingCartService.saveNewBeeMallCartItem(item)).thenReturn(ServiceResultEnum.ERROR.getResult());
        }

        Result result = shoppingCartController.saveNewBeeMallShoppingCartItem(item, mockHttpSession);

        if (expectedResult == 1) {
            assertEquals(200, result.getResultCode());
        } else if (expectedResult == 0) {
            assertEquals(500, result.getResultCode());
        }

    }
}

package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NewBeeMallOrderControllerTest {

    @Autowired
    private NewBeeMallOrderController newBeeMallOrderController;

    @MockBean
    private NewBeeMallOrderService newBeeMallOrderService;

    @ParameterizedTest
    @CsvSource({
            "null,null,'','参数异常！'",
            "0,null,'',参数异常！",
            "1,null,'',参数异常！",
            "1,null,'ABC',参数异常！",
            "null,null,'ABC',参数异常！",
            "0,null,'ABC',参数异常！",
            "null,0,'',参数异常！",
            "null,0,'ABC',参数异常！",
            "null,100,'',参数异常！",
            "null,100,'ABC',参数异常！",
            "0,0,'',参数异常！",
            "0,0,'ABC',参数异常！",
            "0,100,'',参数异常！",
            "0,100,'ABC',参数异常！",
            "1,0,'',参数异常！",
            "1,0,'ABC',参数异常！",
            "1,100,'',参数异常！",
            "100,1,'ABC','SUCCESS'",
    })
    public void testUpdate(String orderIdStr, String totalPriceStr, String userAddress, String expectedResult) {
        Integer orderId = "null".equals(orderIdStr) ? null : Integer.valueOf(orderIdStr);
        Integer totalPrice = "null".equals(totalPriceStr) ? null : Integer.valueOf(totalPriceStr);

        NewBeeMallOrder newBeeMallOrder = new NewBeeMallOrder();

        if (orderId != null && totalPrice != null && orderId >= 1 && totalPrice >= 1 && !StringUtils.isEmpty(userAddress)) {

            newBeeMallOrder.setOrderId(Long.valueOf(orderId));
            newBeeMallOrder.setTotalPrice(totalPrice);
            newBeeMallOrder.setUserAddress(userAddress);
        }

        if ("SUCCESS".equals(expectedResult)) {
            when(newBeeMallOrderService.updateOrderInfo(newBeeMallOrder)).thenReturn(ServiceResultEnum.SUCCESS.getResult());
        } else if ("FAIL".equals(expectedResult)) {
            when(newBeeMallOrderService.updateOrderInfo(newBeeMallOrder)).thenReturn(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }

        Result result = newBeeMallOrderController.update(newBeeMallOrder);

        if ("参数异常！".equals(expectedResult)) {
            assertEquals(expectedResult, result.getMessage());
        } else if ("SUCCESS".equals(expectedResult)) {
            assertEquals(ResultGenerator.genSuccessResult().getMessage(), result.getMessage());
        } else if ("FAIL".equals(expectedResult)) {
            assertEquals(ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult()).getMessage(), result.getMessage());
        }
    }
}

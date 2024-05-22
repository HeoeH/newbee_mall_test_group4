package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.NewBeeMallOrderMapper;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.common.ServiceResultEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class NewBeeMallOrderServiceImplTest {

    @InjectMocks
    private NewBeeMallOrderServiceImpl orderService;

    @Mock
    private NewBeeMallOrderMapper newBeeMallOrderMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            // 测试成功情况
            "success, 2, 1:2, 15688187285093508:15688187285093509, 1:1, 0:0, 2",

            // 测试未查询到订单数据情况
            "未查询到记录！, 0, , , , , 0",

            // 测试订单未支付情况
            "15688187285093508 订单的状态不是支付成功无法执行出库操作, 2, 1:2, 15688187285093508:15688187285093509, 1:1, 1:0, 0",

            // 测试订单未支付情况
            "15688187285093508 订单的状态不是支付成功无法执行出库操作, 2, 1:2, 15688187285093508:15688187285093509, 0:1, 0:0, 0",

    })
    public void testCheckDone(String expected, int numOrders, String orderIds, String orderNos, String orderStatuses, String isDeleteds, int checkDoneReturnValue) {
        List<NewBeeMallOrder> orders = new ArrayList<>();
        if (numOrders > 0) {
            String[] orderIdArray = orderIds.split(":");
            String[] orderNoArray = orderNos.split(":");
            String[] orderStatusArray = orderStatuses.split(":");
            String[] isDeletedArray = isDeleteds.split(":");

            for (int i = 0; i < numOrders; i++) {
                orders.add(createOrder(
                        Long.parseLong(orderIdArray[i]),
                        orderNoArray[i],
                        Byte.parseByte(orderStatusArray[i]),
                        Byte.parseByte(isDeletedArray[i])
                ));
            }
        }

        Long[] ids = orders.stream().map(NewBeeMallOrder::getOrderId).toArray(Long[]::new);

        when(newBeeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids))).thenReturn(orders);

        when(newBeeMallOrderMapper.checkDone(Arrays.asList(ids))).thenReturn(checkDoneReturnValue);

        String result = orderService.checkDone(ids);

        assertEquals(expected, result);
    }

    // 创建订单的辅助方法
    private static NewBeeMallOrder createOrder(Long orderId, String orderNo, Byte orderStatus, Byte isDeleted) {
        NewBeeMallOrder order = new NewBeeMallOrder();
        order.setOrderId(orderId);
        order.setOrderNo(orderNo);
        order.setOrderStatus(orderStatus);
        order.setIsDeleted(isDeleted);
        return order;
    }
}

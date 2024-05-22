package ltd.newbee.mall.controller.mall;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.exception.NewBeeMallException;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.easymock.EasyMock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SaveOrderTest_WhiteBox {
    private HttpSession httpSession = EasyMock.createMock(HttpSession.class);
    private NewBeeMallShoppingCartService newBeeMallShoppingCartService = EasyMock.createMock(NewBeeMallShoppingCartService.class);
    private NewBeeMallOrderService newBeeMallOrderService = EasyMock.createMock(NewBeeMallOrderService.class);
    private NewBeeMallUserVO userVO = EasyMock.createMock(NewBeeMallUserVO.class);
    private Long UserId = 10L;
    private List<NewBeeMallShoppingCartItemVO> myShoppingCartItems;

    private String address;
    private Boolean isShoppingCartEmpty;
    private String expectedResult;

    public SaveOrderTest_WhiteBox(String address, Boolean isShoppingCartEmpty, String expectedResult) {
        this.address = address;
        this.isShoppingCartEmpty = isShoppingCartEmpty;
        this.expectedResult = expectedResult;
        myShoppingCartItems = new ArrayList<>();
        myShoppingCartItems.add(new NewBeeMallShoppingCartItemVO());
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                //测试用例1:地址为空
                { "", false, "地址不能为空！"},
                //测试用例2:购物车数据异常
                { "validAddress", true, "购物车数据异常！"},
                //测试用例3:正常保存订单
                { "validAddress", false, "redirect:/orders/saveOrderNo"}
        });
    }

    @Before
    public void setup() {
        EasyMock.reset(httpSession, newBeeMallShoppingCartService,newBeeMallOrderService,userVO);

        EasyMock.expect((NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY)).andReturn(userVO).once();
        EasyMock.expect(userVO.getUserId()).andReturn(UserId).anyTimes();
        if(isShoppingCartEmpty){
            EasyMock.expect(newBeeMallShoppingCartService.getMyShoppingCartItems(UserId)).andReturn(null).once();
        }
        else{
            EasyMock.expect(newBeeMallShoppingCartService.getMyShoppingCartItems(UserId)).andReturn(myShoppingCartItems).once();
        }

        EasyMock.expect(userVO.getAddress()).andReturn(address).anyTimes();
        EasyMock.expect(newBeeMallOrderService.saveOrder(EasyMock.anyObject(), EasyMock.anyLong(), EasyMock.anyObject())).andReturn("saveOrderNo").anyTimes();

        EasyMock.replay(httpSession,userVO,newBeeMallShoppingCartService,newBeeMallOrderService);
    }

    @Test
    public void testSaveOrder_WhiteBox() {
        OrderController controller = new OrderController();
        controller.setNewBeeMallShoppingCartService(newBeeMallShoppingCartService);
        controller.setNewBeeMallOrderService(newBeeMallOrderService);

        try{
            // 调用方法
            String result = controller.saveOrder(UserId, httpSession);
            // 验证结果
            EasyMock.verify(httpSession,newBeeMallShoppingCartService,newBeeMallOrderService);
            assertEquals(expectedResult, result);
        }catch (NewBeeMallException e){
            assertEquals(expectedResult, e.getMessage());
        }
    }
}

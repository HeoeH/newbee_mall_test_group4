package ltd.newbee.mall.controller.mall;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallOrderStatusEnum;
import ltd.newbee.mall.common.PayStatusEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.AlipayConfig;
import ltd.newbee.mall.config.ProjectConfig;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.exception.NewBeeMallException;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.util.ResultGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.easymock.EasyMock;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PayOrderTest_WhiteBox {
    private HttpServletRequest request = createMock(HttpServletRequest.class);
    private HttpSession httpSession = createMock(HttpSession.class);
    private AlipayConfig alipayConfig = EasyMock.createMock(AlipayConfig.class);
    private NewBeeMallUserVO mallUserVO = createMock(NewBeeMallUserVO.class);
    private NewBeeMallOrder newBeeMallOrder = createMock(NewBeeMallOrder.class);
    private NewBeeMallOrderService newBeeMallOrderService = createMock(NewBeeMallOrderService.class);
    private AlipayClient alipayClient = createMock(AlipayClient.class);
    private AlipayTradePagePayRequest alipayRequest = EasyMock.createMock(AlipayTradePagePayRequest.class);
    private String orderNo = "testOrderNo";
    private Integer totalPrice = 100;

    private Long userId;
    private Long orderUserId;
    private Byte orderStatus;
    private Byte payStatus;
    private int payType;
    private String expectedResult;

    public PayOrderTest_WhiteBox(Long userId, Long orderUserId, int orderStatus, int payStatus, int payType, String expectedResult) {
        this.userId = userId;
        this.orderUserId = orderUserId;
        this.orderStatus = (byte)orderStatus;
        this.payStatus = (byte)payStatus;
        this.payType = payType;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                //测试用例1:抛出订单结算异常
                { 15L, 16L, NewBeeMallOrderStatusEnum.ORDER_PAID.getOrderStatus(), PayStatusEnum.PAY_SUCCESS.getPayStatus(), 1, "当前订单用户异常"},
                //测试用例2:抛出NO_PERMISSION_ERROR异常
                { 15L, 15L, NewBeeMallOrderStatusEnum.ORDER_PAID.getOrderStatus(), PayStatusEnum.PAY_ING.getPayStatus(), 1, "订单结算异常"},
                //测试用例3:抛出NO_PERMISSION_ERROR异常
                { 15L, 15L, NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus(), PayStatusEnum.PAY_SUCCESS.getPayStatus(), 2, "订单结算异常"},
                //测试用例4:跳转到支付宝支付
                { 15L, 15L, NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus(), PayStatusEnum.PAY_ING.getPayStatus(), 1, "mall/alipay"}
        });
    }

    @Before
    public void setup() {
        EasyMock.reset(request, httpSession,alipayConfig, mallUserVO, newBeeMallOrder, newBeeMallOrderService,alipayClient,alipayRequest);

        EasyMock.expect((NewBeeMallUserVO)httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY)).andReturn(mallUserVO).once();
        EasyMock.expect(mallUserVO.getUserId()).andReturn(userId).once();
        EasyMock.expect(newBeeMallOrderService.getNewBeeMallOrderByOrderNo(orderNo)).andReturn(newBeeMallOrder).anyTimes();
        EasyMock.expect(newBeeMallOrder.getUserId()).andReturn(orderUserId).times(2);
        EasyMock.expect(newBeeMallOrder.getOrderStatus()).andReturn(orderStatus).anyTimes();
        EasyMock.expect(newBeeMallOrder.getPayStatus()).andReturn(payStatus).anyTimes();
        request.setAttribute("orderNo", orderNo);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(newBeeMallOrder.getTotalPrice()).andReturn(totalPrice).anyTimes();
        request.setAttribute("totalPrice", totalPrice);
        EasyMock.expectLastCall().anyTimes();

        if(payType == 1){
            try{
                AlipayTradePagePayResponse alipayResponse = EasyMock.createMock(AlipayTradePagePayResponse.class);

                request.setCharacterEncoding(Constants.UTF_ENCODING);
                EasyMock.expectLastCall().anyTimes();

                EasyMock.expect(request.getContextPath()).andReturn("any").anyTimes();
                EasyMock.expect(newBeeMallOrder.getOrderNo()).andReturn(orderNo).anyTimes();

                alipayRequest.setReturnUrl(ProjectConfig.getServerUrl() + "any" + "/returnOrders/" + orderNo + "/" + userId);
                EasyMock.expectLastCall().anyTimes();
                alipayRequest.setNotifyUrl(ProjectConfig.getServerUrl() + "any" + "/paySuccess?payType=1&orderNo=" + orderNo);
                EasyMock.expectLastCall().anyTimes();
                alipayRequest.setBizContent(EasyMock.anyString());
                EasyMock.expectLastCall().anyTimes();

                EasyMock.expect(alipayConfig.getGateway()).andReturn("https://openapi.alipay.com/gateway.do").anyTimes();
                EasyMock.expect(alipayConfig.getAppId()).andReturn("appId").anyTimes();
                EasyMock.expect(alipayConfig.getRsaPrivateKey()).andReturn(null).anyTimes();
                EasyMock.expect(alipayConfig.getFormat()).andReturn("json").anyTimes();
                EasyMock.expect(alipayConfig.getCharset()).andReturn("UTF-8").anyTimes();
                EasyMock.expect(alipayConfig.getAlipayPublicKey()).andReturn("alipayPublicKey").anyTimes();
                EasyMock.expect(alipayConfig.getSigntype()).andReturn("RSA").anyTimes();

                EasyMock.expect(alipayClient.pageExecute(alipayRequest)).andReturn(alipayResponse).anyTimes();
                EasyMock.expect(alipayResponse.getBody()).andReturn("any").anyTimes();

                request.setAttribute("form", "any");
                EasyMock.expectLastCall().anyTimes();

                EasyMock.replay(alipayResponse);
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        }

        EasyMock.replay(request, httpSession,alipayConfig, mallUserVO, newBeeMallOrder,newBeeMallOrderService,alipayClient,alipayRequest);
    }

    @Test
    public void testLogin_WhiteBox() {
        OrderController controller = new OrderController();
        controller.setNewBeeMallOrderService(newBeeMallOrderService);
        controller.setAlipayConfig(alipayConfig);
        controller.setAlipayClient(alipayClient);
        controller.setAlipayRequest(alipayRequest);
        try{
            // 调用方法
            String result = controller.payOrder(request, orderNo, httpSession, payType);
            // 验证结果
            EasyMock.verify(request, httpSession,alipayConfig, mallUserVO, newBeeMallOrder, newBeeMallOrderService,alipayClient,alipayRequest);
            assertEquals(expectedResult, result);
        }catch (NewBeeMallException | UnsupportedEncodingException e){
            assertEquals(expectedResult, e.getMessage());
        } finally {

        }
    }
}

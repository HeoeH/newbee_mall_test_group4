package ltd.newbee.mall.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.controller.vo.SeckillSuccessVO;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.dao.NewBeeMallSeckillMapper;
import ltd.newbee.mall.dao.NewBeeMallSeckillSuccessMapper;
import ltd.newbee.mall.entity.NewBeeMallSeckill;
import ltd.newbee.mall.entity.NewBeeMallSeckillSuccess;
import ltd.newbee.mall.exception.NewBeeMallException;
import ltd.newbee.mall.redis.RedisCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class NewBeeMallSeckillServiceImplTest {

    @InjectMocks
    private NewBeeMallSeckillServiceImpl seckillService;
    @Mock
    private  RateLimiter RATE_LIMITER = RateLimiter.create(100);
    @Mock
    private NewBeeMallSeckillMapper newBeeMallSeckillMapper;
    @Mock
    private NewBeeMallSeckillSuccessMapper newBeeMallSeckillSuccessMapper;
    @Mock
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;
    @Mock
    private RedisCache redisCache;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "秒杀失败, 10812, 18, false, false, false, 10, true, 1622556000000, 1622642400000, 1622600000000, -2",
            "您已经购买过秒杀商品，请勿重复购买, 10812, 18, true, true, false, 10, true, 1622556000000, 1622642400000, 1622600000000, -2",
            "秒杀商品已售空, 10812, 18, true, false, false, -1, true, 1622556000000, 1622642400000, 1622600000000, -2",
            "秒杀未开启, 10812, 18, true, false, false, 10, true, 1622550000000, 1622642400000, 1622540000000, -2",
            "秒杀已结束, 10812, 18, true, false, false, 10, true, 1622556000000, 1622642400000, 1622650000000, -2",
            "很遗憾！未抢购到秒杀商品, 10812, 18, true, false, false, 10, true, 1622556000000, 1622642400000, 1622600000000, 0",
            "成功, 10812, 18, true, false, false, 10, true, 1622556000000, 1622642400000, 1622600000000, 1"
    })
    public void testExecuteSeckill(String expectedMessage, Long seckillId, Long userId, boolean getToken, boolean userPurchased, boolean seckillNull, long stock, boolean seckillFromDb, long beginTime, long endTime, long currentTime, int procedureResult) {
        when(RATE_LIMITER.tryAcquire(500, TimeUnit.MILLISECONDS)).thenReturn(getToken);

        when(redisCache.containsCacheSet(Constants.SECKILL_SUCCESS_USER_ID + seckillId, userId)).thenReturn(userPurchased);
        when(redisCache.luaDecrement(Constants.SECKILL_GOODS_STOCK_KEY + seckillId)).thenReturn(stock);

        NewBeeMallSeckill mockSeckill = newBeeMallSeckill();
        mockSeckill.setSeckillBegin(new Date(beginTime));
        mockSeckill.setSeckillEnd(new Date(endTime));
        if (!seckillNull) {
            when(redisCache.getCacheObject(Constants.SECKILL_KEY + seckillId)).thenReturn(mockSeckill);
        } else {
            when(redisCache.getCacheObject(Constants.SECKILL_KEY + seckillId)).thenReturn(null);
            if (seckillFromDb) {
                when(newBeeMallSeckillMapper.selectByPrimaryKey(seckillId)).thenReturn(mockSeckill);
            }
        }

        Date now = new Date(currentTime);

        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("userId", userId);
        map.put("killTime", now);
        map.put("result", procedureResult);
        doAnswer(invocation -> {
            Map<String, Object> args = invocation.getArgument(0);
            args.put("result", procedureResult);
            return null;
        }).when(newBeeMallSeckillMapper).killByProcedure(any());

        NewBeeMallSeckillSuccess seckillSuccess = new NewBeeMallSeckillSuccess();
        seckillSuccess.setSecId(123L);
        when(newBeeMallSeckillSuccessMapper.getSeckillSuccessByUserIdAndSeckillId(userId, seckillId)).thenReturn(seckillSuccess);

        try {
            SeckillSuccessVO result = seckillService.executeSeckill(seckillId, userId, currentTime);
            if ("成功".equals(expectedMessage)) {
                assertNotNull(result);
            } else {
                fail("没有抛出期望的异常");
            }
        } catch (NewBeeMallException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    private NewBeeMallSeckill newBeeMallSeckill() {
        NewBeeMallSeckill seckill = new NewBeeMallSeckill();
        return seckill;
    }
}
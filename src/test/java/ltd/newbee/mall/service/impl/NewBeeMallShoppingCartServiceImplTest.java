package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.dao.NewBeeMallShoppingCartItemMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;
import ltd.newbee.mall.service.impl.NewBeeMallShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewBeeMallShoppingCartServiceImplTest {

    @Mock
    private NewBeeMallShoppingCartItemMapper newBeeMallShoppingCartItemMapper;

    @Mock
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;

    @InjectMocks
    private NewBeeMallShoppingCartServiceImpl newBeeMallShoppingCartService;

    @BeforeEach
    void setUp() {
        // Setup logic if needed
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 1, 0, true, false, success",                       // 商品不存在于购物车，新建并插入成功
            "1, 1, 1, 0, false, false, 商品不存在！",              // 商品不存在于购物车，但商品信息不存在
            "1, 1, 11, 0, true, false, 超出单个商品的最大购买数量！",  // 商品数量超过单个商品的最大限制
            "1, 1, 1, 21, true, false, 超出购物车最大容量！",  // 总商品数量超过购物车的最大限制
            "1, 1, 1, 0, true, false, database error",                     // 插入操作失败
            "1, 1, 5, 0, true, true, success"                        // 商品已存在于购物车，更新数量成功
    })
    void testSaveNewBeeMallCartItem(Long userId, Long goodsId, int goodsCount, int existingItemsCount, boolean goodsExist, boolean cartItemExist, String expected) {
        NewBeeMallShoppingCartItem cartItem = new NewBeeMallShoppingCartItem();
        cartItem.setUserId(userId);
        cartItem.setGoodsId(goodsId);
        cartItem.setGoodsCount(goodsCount);

        NewBeeMallGoods goods = goodsExist ? new NewBeeMallGoods() : null;
        NewBeeMallShoppingCartItem existingCartItem = cartItemExist ? new NewBeeMallShoppingCartItem() : null;

        lenient().when(newBeeMallShoppingCartItemMapper.selectByUserIdAndGoodsId(cartItem.getUserId(), cartItem.getGoodsId())).thenReturn(existingCartItem);
        lenient().when(newBeeMallGoodsMapper.selectByPrimaryKey(cartItem.getGoodsId())).thenReturn(goods);
        lenient().when(newBeeMallShoppingCartItemMapper.selectCountByUserId(cartItem.getUserId())).thenReturn(existingItemsCount);

        if (expected.equals("database error")) {
            lenient().when(newBeeMallShoppingCartItemMapper.insertSelective(any())).thenReturn(0);
        } else {
            lenient().when(newBeeMallShoppingCartItemMapper.insertSelective(any())).thenReturn(1);
        }

        if (cartItemExist) {
            lenient().when(newBeeMallShoppingCartItemMapper.selectByPrimaryKey(any())).thenReturn(cartItem);
            lenient().when(newBeeMallShoppingCartItemMapper.updateByPrimaryKeySelective(any())).thenReturn(1);
        }

        String result = newBeeMallShoppingCartService.saveNewBeeMallCartItem(cartItem);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 5, 5, false, true, 未查询到记录！",                       // 更新的购物车项不存在
            "1, 1, 5, 6, true, true, 超出单个商品的最大购买数量！", // 商品数量超过单个商品的最大限制
            "1, 1, 5, 5, true, true, success",                               // 商品数量相同，不需要修改
            "1, 3, 4, 5, true, false, 无权限！",                   // 购物车项用户ID不同，没有权限修改
            "1, 1, 6, 5, true, true, success",                               // 成功更新购物车项
            "1, 1, 5, 4, true, true, database error"                               // 更新数据库失败
    })
    void testUpdateNewBeeMallCartItem(Long userId, Long cartItemId, int oldGoodsCount, int newGoodsCount, boolean exist, boolean right, String expected) {
        NewBeeMallShoppingCartItem cartItem = new NewBeeMallShoppingCartItem();
        cartItem.setCartItemId(cartItemId);
        cartItem.setUserId(userId);
        cartItem.setGoodsCount(newGoodsCount);

        NewBeeMallShoppingCartItem existingCartItem = exist ? new NewBeeMallShoppingCartItem() : null;
        if (existingCartItem != null) {
            existingCartItem.setCartItemId(cartItemId);
            if (right)
                existingCartItem.setUserId(userId);
            else
                existingCartItem.setUserId(userId + 1);
            existingCartItem.setGoodsCount(oldGoodsCount);
        }

        when(newBeeMallShoppingCartItemMapper.selectByPrimaryKey(anyLong())).thenReturn(existingCartItem);

        if (expected.equals("database error")) {
            lenient().when(newBeeMallShoppingCartItemMapper.updateByPrimaryKeySelective(any())).thenReturn(0);
        } else {
            lenient().when(newBeeMallShoppingCartItemMapper.updateByPrimaryKeySelective(any())).thenReturn(1);
        }

        String result = newBeeMallShoppingCartService.updateNewBeeMallCartItem(cartItem);

        assertEquals(expected, result);
    }
}
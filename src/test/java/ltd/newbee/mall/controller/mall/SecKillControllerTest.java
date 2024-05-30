package ltd.newbee.mall.controller.mall;


import ltd.newbee.mall.exception.NewBeeMallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SecKillControllerTest {

    @Autowired
    private SecKillController secKillController;

    @Test
    @ParameterizedTest
    @CsvSource({
            "22, 15, c4ca4238a0b923820dcc509a6f75849b, 1",
            "3, 2, , 0",
            "1, 1, a87ff679a2f3e71d9181a67b7542122c, 0",
            "4, , a87ff679a2f3e71d9181a67b7542122c, 0",
            "5, 1a, e4da3b7fbbce2345d7772b0674a318d5, 0"
    })
    void testExecute(String seckillIds, String userIds, String md5, boolean expectedResult) {
        try {
            Long seckillId = Long.parseLong(seckillIds);
            Long userId = Long.parseLong(userIds);
            secKillController.execute(seckillId, userId, md5);
            assertTrue(expectedResult);
        } catch (NewBeeMallException e) {
            assertFalse(expectedResult);
            assertEquals("秒杀商品不存在", e.getMessage());
        } catch (NullPointerException npe) {
            assertFalse(expectedResult);
        } catch (NumberFormatException nfe) {
            assertFalse(expectedResult);
        }
    }
}

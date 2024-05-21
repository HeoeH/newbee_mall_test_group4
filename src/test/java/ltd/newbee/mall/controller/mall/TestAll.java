package ltd.newbee.mall.controller.mall;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginTest_WhiteBox.class,
        LogoutTest_WhiteBox.class,
        UpdateInfoTest_WhiteBox.class,
        PayOrderTest_WhiteBox.class,
        SaveOrderTest_WhiteBox.class
})
public class TestAll {
}

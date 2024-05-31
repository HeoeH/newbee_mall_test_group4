package ltd.newbee.mall.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AdminUserServiceTest.class,
        AdminUserControllerTest.class,
        AdminUserControllerTest2.class,
        AdminUserServiceTest2.class,
        AdminControllerTest3.class,
        AdminControllerTest.class
})
public class AllTestsSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}

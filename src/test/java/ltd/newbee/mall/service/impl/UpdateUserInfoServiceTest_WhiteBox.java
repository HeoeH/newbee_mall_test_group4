package ltd.newbee.mall.service.impl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.exception.NewBeeMallException;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.service.NewBeeMallUserService;
import ltd.newbee.mall.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.easymock.EasyMock;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class UpdateUserInfoServiceTest_WhiteBox {
    private HttpSession httpSession = EasyMock.createMock(HttpSession.class);
    private MallUserMapper mallUserMapper = EasyMock.createMock(MallUserMapper.class);
    private NewBeeMallUserVO userTemp = EasyMock.createMock(NewBeeMallUserVO.class);
    private MallUser userFromDB = EasyMock.createMock(MallUser.class);
    private MallUser mallUser = EasyMock.createMock(MallUser.class);
    private NewBeeMallUserVO newBeeMallUserVO = EasyMock.createMock(NewBeeMallUserVO.class);

    private Boolean isValidUser;
    private String nickName;
    private String address;
    private String introduceSign;
    private Boolean selectable;
    private String expectedResult;

    private final String nickNameFromDB = "originName";
    private final String addressFromDB = "originAddress";
    private final String introduceSignFromDB = "originIntroduceSign";

    public UpdateUserInfoServiceTest_WhiteBox(Boolean isValidUser, String nickName, String address, String introduceSign, Boolean selectable, String expectedResult) {
        this.isValidUser = isValidUser;
        this.nickName = nickName;
        this.address = address;
        this.introduceSign = introduceSign;
        this.selectable = selectable;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                //测试用例1:更改成功
                {true, "newName", "newAddress", "newIntroduceSign", true, "success"},
                //测试用例2:个人信息无变更
                {true, "originName", "originAddress", "originIntroduceSign", true, "个人信息无变更！"},
                //测试用例3:数据库查询失败
                {false, "newName", "newAddress", "newIntroduceSign", true, null},
                //测试用例4:未知异常
                {true, "newName", "newAddress", "newIntroduceSign", false, null}
        });
    }

    @Before
    public void setUp(){
        EasyMock.reset(httpSession, mallUserMapper, userTemp, userFromDB, mallUser, newBeeMallUserVO);

        EasyMock.expect((NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY)).andReturn(userTemp).once();
        EasyMock.expect(userTemp.getUserId()).andReturn(1L).anyTimes();
        if(isValidUser){
            EasyMock.expect(mallUserMapper.selectByPrimaryKey(EasyMock.anyLong())).andReturn(userFromDB).once();
        }else{
            EasyMock.expect(mallUserMapper.selectByPrimaryKey(EasyMock.anyLong())).andReturn(null).once();
        }
        EasyMock.expect(mallUser.getNickName()).andReturn(nickName).anyTimes();
        EasyMock.expect(mallUser.getAddress()).andReturn(address).anyTimes();
        EasyMock.expect(mallUser.getIntroduceSign()).andReturn(introduceSign).anyTimes();
        EasyMock.expect(userFromDB.getNickName()).andReturn(nickNameFromDB).anyTimes();
        EasyMock.expect(userFromDB.getAddress()).andReturn(addressFromDB).anyTimes();
        EasyMock.expect(userFromDB.getIntroduceSign()).andReturn(introduceSignFromDB).anyTimes();

        if(selectable){
            EasyMock.expect(mallUserMapper.updateByPrimaryKeySelective(userFromDB)).andReturn(1).anyTimes();
        }else{
            EasyMock.expect(mallUserMapper.updateByPrimaryKeySelective(userFromDB)).andReturn(0).anyTimes();
        }

        userFromDB.setNickName(NewBeeMallUtils.cleanString(EasyMock.anyString()));
        EasyMock.expectLastCall().anyTimes();
        userFromDB.setAddress(NewBeeMallUtils.cleanString(EasyMock.anyString()));
        EasyMock.expectLastCall().anyTimes();
        userFromDB.setIntroduceSign(NewBeeMallUtils.cleanString(EasyMock.anyString()));
        EasyMock.expectLastCall().anyTimes();

//        BeanUtil.copyProperties(userFromDB, newBeeMallUserVO);
//        EasyMock.expectLastCall().anyTimes();
//        httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
//        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(httpSession, mallUserMapper, userTemp, userFromDB, mallUser, newBeeMallUserVO);
    }

    @Test
    public void testUpdateUserInfoService_WhiteBox(){
        NewBeeMallUserServiceImpl service = new NewBeeMallUserServiceImpl();
        service.setMallUserMapper(mallUserMapper);

        try{
            NewBeeMallUserVO newBeeMallUserVO = service.updateUserInfo(mallUser, httpSession);
            EasyMock.verify(httpSession, mallUserMapper, userTemp, mallUser);
            if(expectedResult != null){
                assertNotNull(newBeeMallUserVO);
            }else{
                assertNull(newBeeMallUserVO);
            }
        }catch (NewBeeMallException e){
            assertEquals(expectedResult, e.getMessage());
        }
    }
}
package ltd.newbee.mall.controller.mall;

import jakarta.servlet.http.HttpSession;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试开放接口，用于解决验证码相关的问题
 */
@Controller
public class CaptchaCodeController {

    // 接口：修改会话属性
    @PostMapping("/setSessionAttribute")
    @ResponseBody
    public Result setSessionAttribute(HttpSession session,
                                      @RequestParam String attributeName,
                                      @RequestParam String attributeValue) {
        session.setAttribute(attributeName, attributeValue);
        return ResultGenerator.genSuccessResult();
    }

    // 接口：获取会话属性
    @GetMapping("/getSessionAttribute")
    @ResponseBody
    public String getSessionAttribute(HttpSession session,
                                      @RequestParam String attributeName) {
        Object attributeValue = session.getAttribute(attributeName);
        return (attributeValue != null ? attributeValue.toString() : "No attribute set");
    }
}

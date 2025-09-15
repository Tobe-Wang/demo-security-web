package cn.zhaofd.demosecurityweb.modules.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 其它角色认证接口
 */
@RestController
@RequestMapping("/otherAuth")
public class OtherAuthController {
    @GetMapping(value = "/test")
    public String getById() {
        return "otherAuth";
    }
}

package cn.zhaofd.demosecurityweb.modules.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 普通角色认证接口
 */
@RestController
@RequestMapping("/userAuth")
public class UserAuthController {
    @GetMapping(value = "/test")
    public String getById() {
        return "userAuth";
    }
}

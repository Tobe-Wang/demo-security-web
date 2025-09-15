package cn.zhaofd.demosecurityweb.modules.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员角色认证接口
 */
@RestController
@RequestMapping("/adminAuth")
public class AdminAuthController {
    @GetMapping(value = "/test")
    public String getById() {
        return "adminAuth";
    }
}

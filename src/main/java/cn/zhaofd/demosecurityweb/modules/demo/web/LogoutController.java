/*
 * Copyright (c) 2025. Tobe Wang
 */

package cn.zhaofd.demosecurityweb.modules.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 登出控制器
 */
@Controller
@RequestMapping("/logout")
public class LogoutController {
    /**
     * 登出页
     *
     * @return String
     */
    @GetMapping("/index")
    public String page() {
        return "logout/page";
    }

    /**
     * 登出后结果页
     *
     * @return String
     */
    @GetMapping("/result")
    public String result() {
        return "logout/result";
    }
}
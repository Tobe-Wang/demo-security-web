/*
 * Copyright (c) 2025. Tobe Wang
 */

package cn.zhaofd.demosecurityweb.modules.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    /**
     * 登录页
     *
     * @return ModelAndView
     */
    @GetMapping("/page")
    public ModelAndView page() {
        return new ModelAndView("login/page");
    }

    /**
     * 欢迎页
     *
     * @return ModelAndView
     */
    @GetMapping("/welcome")
    public ModelAndView welcome() {
        return new ModelAndView("login/welcome");
    }
}
/*
 * Copyright (c) 2025. Tobe Wang
 */

package cn.zhaofd.demosecurityweb.modules.demo.service;

import cn.zhaofd.demosecurityweb.modules.demo.dto.SysUser;
import cn.zhaofd.demosecurityweb.modules.demo.dto.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;

    public UserService(@Autowired PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public SysUser getUserByName(String username) {
        UserRole userRole1 = new UserRole();
        userRole1.setId(1);
        userRole1.setRoleName("ROLE_USER");
        UserRole userRole2 = new UserRole();
        userRole2.setId(2);
        userRole2.setRoleName("ROLE_ADMIN");

        SysUser sysUser1 = new SysUser();
        sysUser1.setId(1);
        sysUser1.setUserName("test1");
        String pwd1 = passwordEncoder.encode("123456");
        logger.info("【Spring Security】用户名{}，加密生成的密码{}，长度{}", sysUser1.getUserName(), pwd1, pwd1.length());
        sysUser1.setPwd(pwd1);
        sysUser1.setAvailable(1);
        sysUser1.setRoleList(List.of(userRole1));

        SysUser sysUser2 = new SysUser();
        sysUser2.setId(2);
        sysUser2.setUserName("admin");
        String pwd2 = passwordEncoder.encode("asdfgh");
        logger.info("【Spring Security】用户名{}，加密生成的密码{}，长度{}", sysUser2.getUserName(), pwd2, pwd2.length());
        sysUser2.setPwd(pwd2);
        sysUser2.setAvailable(1);
        sysUser2.setRoleList(List.of(userRole1, userRole2));

        if ("test1".equals(username)) {
            return sysUser1;
        } else if ("admin".equals(username)) {
            return sysUser2;
        }
        return null;
    }
}

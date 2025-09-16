/*
 * Copyright (c) 2025. Tobe Wang
 */

package cn.zhaofd.demosecurityweb.config;

import cn.zhaofd.demosecurityweb.modules.demo.dto.SysUser;
import cn.zhaofd.demosecurityweb.modules.demo.dto.UserRole;
import cn.zhaofd.demosecurityweb.modules.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security配置
 */
@Configuration
public class SpringSecurityConfig {
    private final PropertyConfig propertyConfig;

    public SpringSecurityConfig(@Autowired PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    /**
     * 创建密码编码器
     *
     * @return 创建密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        /*
        // 方式一、BCryptPasswordEncoder密码编码器(Spring Security不可逆的加密方法)，加密后密文长度60
        return new BCryptPasswordEncoder();
        */

        // 方式二、Pbkdf2PasswordEncoder密码编码器(Spring Security不可逆的加密方法，通过密码编辑器密钥加密)
        // Spring Security的Pbkdf2PasswordEncoder密码编辑器密钥
        // 16：盐值长度（范围建议8-32，默认推荐16）
        // 310000：迭代次数(<10000易被GPU暴力破解，50000-100000勉强可用，默认推荐310000)，主要影响计算性能(耗时)
        // 配置16,310000    加密后密文长度160
        String secret = propertyConfig.getValue("spring.security.encoder.secret");
        return new Pbkdf2PasswordEncoder(secret, 16, 10000, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
    }

//    /**
//     * 使用内存用户信息服务，设置登录用户名、密码和角色权限
//     *
//     * @return 内存用户信息服务
//     */
//    @Bean
//    public UserDetailsService userDetailsService(@Autowired PasswordEncoder passwordEncoder) {
//        // 定义角色权限，必须ROLE_开头
//        GrantedAuthority roleUser = () -> "ROLE_USER"; // 用户角色权限
//        GrantedAuthority roleAdmin = () -> "ROLE_ADMIN"; // 管理员角色权限
//
//        // 创建用户列表
//        List<UserDetails> userList = List.of(
//                // 创建普通用户
//                new User("user1", passwordEncoder.encode("123456"), List.of(roleUser)),
//                // 创建管理员用户，赋予多个角色权限
//                new User("admin", passwordEncoder.encode("abcdef"), List.of(roleUser, roleAdmin)));
//        return new InMemoryUserDetailsManager(userList);
//    }

    /**
     * 自定义用户信息服务
     *
     * @param userService 用户服务类
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService(@Autowired UserService userService) {
        return username -> { // 使用Lambda表达式
            // 获取数据库用户信息
            SysUser sysUser = userService.getUserByName(username);
            if (sysUser == null) {
                return null;
            }

            // 角色权限列表
            List<GrantedAuthority> authList = new ArrayList<>();
            // 转换为权限列表
            if (sysUser.getRoleList() != null) {
                for (UserRole role : sysUser.getRoleList()) {
                    //noinspection Convert2MethodRef
                    GrantedAuthority auth = () -> role.getRoleName();
                    authList.add(auth);
                }
            }
            // 创建用户详情
            return new User(sysUser.getUserName(), sysUser.getPwd(), authList);
        };
    }

    /**
     * 配置SecurityFilterChain对象
     * <br />〓测试地址〓
     * <br /><a href="http://localhost:8080/adminAuth/test">adminAuth</a>
     * <br /><a href="http://localhost:8080/userAuth/test">userAuth</a>
     * <br /><a href="http://localhost:8080/otherAuth/test">otherAuth</a>
     * <br /><a href="http://localhost:8080/login/page">登录页</a>
     * <br /><a href="http://localhost:8080/login/account">登录验证接口</a>
     * <br /><a href="http://localhost:8080/logout/index">登出页</a>
     *
     * @param http 这个参数Spring Security会自动装配
     * @return SecurityFilterChain对象
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Spring Security 6.1+，配置方式已全面转向Lambda DSL风格，弃用了传统的链式调用和and()方法
        return http
                // 1、授权配置
                .authorizeHttpRequests(auth -> auth
                                // 允许任何人（包括未认证的匿名用户）自由访问
                                .requestMatchers("/login/page", "/logout/result", "/login/account").permitAll()
                                // 限定"/file/**"下所有请求赋予角色ROLE_USER或者ROLE_ADMIN
                                .requestMatchers("/userAuth/**").hasAnyRole("USER", "ADMIN") // hasAnyRoleO方法会默认加入前缀ROLE_
                                // 限定"/excel/**"下所有请求权限赋予角色ROLE_ADMIN
//                                .requestMatchers("/adminAuth/**", "/actuator/**").hasAuthority("ROLE_ADMIN") // hasAuthority()方法不会加入任何前缀
                                .requestMatchers("/adminAuth/**", "/actuator/**").access(this.verifyAuth("ROLE_ADMIN")) // 自定义验证方法
                                // 【对于所有未被前面规则匹配的请求路径】都需要认证
                                .anyRequest().authenticated()
//                                // 【对于所有未被前面规则匹配的请求路径】都允许任何人（包括未认证的匿名用户）自由访问，不需要任何权限验证
//                                .anyRequest().permitAll()
//                                // 【对于所有未被前面规则匹配的请求路径】拒绝访问
//                                .anyRequest().denyAll()
                )
                // 2、匿名访问配置
//                .anonymous(withDefaults()) // 使用默认匿名配置(对于没有配置权限的其他请求允许匿名访问)
//                .anonymous(anon -> anon.disable()) // 禁用匿名访问
                // 3、启用”记住我“的功能
                .rememberMe(rem -> rem.tokenValiditySeconds(86400) // 超时时间为1天（86400秒）
                        .key("pwd@123QWE") // 设置Remember-Me令牌的安全密钥
                        .rememberMeParameter("remember-me") // 指定登录表单中"记住我"复选框的参数名(对应name="remember-me")
                        .rememberMeCookieName("remember_me") // 设置存储在浏览器中的Remember-Me cookie名称
                        .tokenRepository(getPersistentTokenRepository()) // 添加持久化token存储
                        .useSecureCookie(true) // true：Cookie只能通过HTTPS加密连接传输;false：Cookie可以通过HTTP或HTTPS传输（包括未加密连接）
                )
                // 4、表单登录配置
//                .formLogin(withDefaults()) // 使用默认表单登录页
                .formLogin(form -> form.loginPage("/login/page") // 自定义登录页面url
                        .loginProcessingUrl("/login/account") // 自定义登录处理地址(表单post提交登录地址)，默认为/login/page(与自定义登录页面url相同)
                        .usernameParameter("username") // 登录表单中用户名输入框的参数名(对应name="username")，默认为username
                        .passwordParameter("password") // 登录表单中密码输入框的参数名(对应name="password")，默认为password
                        .defaultSuccessUrl("/login/welcome") // 登录成功后跳转的页面url
                ) // 自定义登录
                // 5、退出登录配置
                .logout(logout -> logout.logoutUrl("/logout/page") // 自定义登出处理地址(表单post提交登录地址)，默认为/logout
                        .logoutSuccessUrl("/logout/result") // 登出成功后跳转的页面url
                ) // 自定义登出
                // 6、HTTP基础认证配置
//                .httpBasic(withDefaults()) // 启用HTTP基础认证（HTTP Basic验证是浏览器自动弹出简单的模态对话框的功能）
                // 7、session管理配置
                .sessionManagement(sm -> sm.invalidSessionUrl("/logout/result")) // session超时跳转的页面url
                // 8、 禁用CSRF过滤器验证(默认不配置为启用)
//                .csrf(AbstractHttpConfigurer::disable) // 防止跨站点请求伪造(基本认证通常禁用CSRF)
                // 9、CORS跨域配置
//                .cors(withDefaults())
                // 10、创建SecurityFilterChain对象，返回
                .build(); // 直接构建，不再需要and()
    }

    /**
     * 配置不拦截的请求路径
     *
     * @return WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring().requestMatchers("/js/**", "/images/**");
        };
    }

    /**
     * 为Spring Security的“记住我”（Remember-Me）功能提供持久化令牌的存储机制
     */
    @Bean
    public PersistentTokenRepository getPersistentTokenRepository() {
        // 内存存储（适合开发环境）
        // 生产环境应使用JdbcTokenRepositoryImpl并配置数据源
        return new InMemoryTokenRepositoryImpl();
    }

    /**
     * 自定义角色权限验证逻辑
     * <br />可以通过获取请求的HttpServletRequest对象、请求参数等进行自定义验证判定
     *
     * @param roleNames 角色列表
     * @return AuthorizationManager
     */
    private AuthorizationManager<RequestAuthorizationContext> verifyAuth(String... roleNames) {
        // 输入参数验证
        if (roleNames == null || roleNames.length == 0) {
            throw new RuntimeException("角色列表不能为空");
        }

        // 转换为列表对象
        var roleNameList = List.of(roleNames);
        return (authSupplier, reqAuthContext) -> {
//            HttpServletRequest request = reqAuthContext.getRequest(); // 获取HttpServletRequest对象
//            Map<String, String> vars = reqAuthContext.getVariables(); // 获取请求参数
            var auths = authSupplier.get().getAuthorities(); // 当前用户的角色权限信息

            for (var auth : auths) {
                var roleName = auth.getAuthority();
                // 当前用户存在对应的角色，放行请求
                if (roleNameList.contains(roleName)) {
                    return new AuthorizationDecision(true);
                }
            }
            // 当前用户不存在对应的角色，不放行请求
            return new AuthorizationDecision(false);
        };
    }
}

/*
 * Copyright (c) 2025. Tobe Wang
 */

package cn.zhaofd.demosecurityweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * 系统自定义属性配置
 */
@Configuration
@EnableConfigurationProperties // 启用属性文件的配置机制，可以将自定义属性配置文件引入到Spring上下文中，使用@Value注入
public class PropertyConfig {
    private final Environment environment;

    public PropertyConfig(@Autowired Environment environment) {
        this.environment = environment;
    }

    /**
     * 获取系统自定义配置属性值
     *
     * @param key 属性键
     * @return 属性值
     */
    public String getValue(String key) {
        return environment.getProperty(key);
    }

    /**
     * 属性配置占位符解析器
     * <br />使用@Value注入，用到Spring EL表达式占位符${...}需配置
     *
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * 系统自定义公共属性配置
     */
    @Configuration
    @PropertySource(value = "classpath:property/param.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
    public static class ParamConfig {
    }

    /**
     * 系统自定义开发环境专有属性配置
     */
    @Configuration
    @Profile("dev") // 仅当application.yml中spring.profiles.active=dev激活时生效
    @PropertySource(value = "classpath:property/param-dev.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
    public static class ParamDevConfig {
    }

    /**
     * 系统自定义生产环境专有属性配置
     */
    @Configuration
    @Profile("prod") // 仅当application.yml中spring.profiles.active=prod激活时生效
    @PropertySource(value = "classpath:property/param-prod.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
    public static class ParamProdConfig {
    }
}

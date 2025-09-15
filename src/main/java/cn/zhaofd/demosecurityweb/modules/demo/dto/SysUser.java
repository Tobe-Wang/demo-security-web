/*
 * Copyright (c) 2025. Tobe Wang
 */

package cn.zhaofd.demosecurityweb.modules.demo.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SysUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String userName;

    private String pwd;

    private Integer available; // 1：可用 0：禁用

    private List<UserRole> roleList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public List<UserRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<UserRole> roleList) {
        this.roleList = roleList;
    }
}
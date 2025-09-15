/*
 * Copyright (c) 2025. Tobe Wang
 */

package cn.zhaofd.demosecurityweb.modules.demo.dto;

import java.io.Serial;
import java.io.Serializable;

public class UserRole implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String roleName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

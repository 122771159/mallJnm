package com.jnm.mallJnm.model.enums;


public enum UserType {
    SUPER("超级管理员"),
    ADMIN("管理员"),
    SALES("业务员"),
    CUSTOMER("客户");

    private final String name;

    UserType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

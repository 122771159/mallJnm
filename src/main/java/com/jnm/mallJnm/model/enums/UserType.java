package com.jnm.mallJnm.model.enums;


public enum UserType {
    ADMIN("管理员"),

    SALESMAN("业务员"),

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

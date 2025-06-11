package com.jnm.mallJnm.model.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING_APPROVAL("待审批"),
    PROCESSING("处理中"),
    SHIPPED("已发货"),
    COMPLETED("已下单"),
    CANCELLED("已作废");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return description;
    }
}
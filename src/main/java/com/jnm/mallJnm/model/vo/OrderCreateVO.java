package com.jnm.mallJnm.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateVO {

    private String customerId;      // 客户ID
    private String addressId;       // 客户选择的收货地址ID
    private List<CartItemVO> items; // 购物车商品列表
    private String customerRemark;  // 客户备注

    @Data
    public static class CartItemVO {
        private String productId; // 商品ID
        private Integer quantity; // 购买数量
    }
}
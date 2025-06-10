package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("jnm_order_item")
public class OrderItem {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String orderId;

    private String productId;

    private String productName;

    private String productImage;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal lineTotalAmount;
}
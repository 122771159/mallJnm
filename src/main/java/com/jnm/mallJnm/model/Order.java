package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jnm_order")
public class Order {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String orderNo;

    private String customerId;

    private String salesId;

    private BigDecimal productAmount;

    private BigDecimal shippingFee;

    private BigDecimal totalAmount;

    private String status;

    private String shippingName;

    private String shippingPhone;

    private String shippingAddress;

    private String customerRemark;

    private String adminRemark;
    // 作废原因
    private String reason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
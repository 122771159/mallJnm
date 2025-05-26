package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerProductPrice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerId;
    private Long productId;
    private BigDecimal customPrice;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
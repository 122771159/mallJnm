package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("jnm_sales_customer")
public class SalesCustomer {

    /**
     * 关联关系ID (UUID)
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 业务员ID (关联jnm_admin表ID, 类型需为SALESMAN)
     */
    private String aid; // 直接对应数据库列名 aid

    /**
     * 客户ID (关联jnm_customer表ID)
     */
    private String cid; // 直接对应数据库列名 cid

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
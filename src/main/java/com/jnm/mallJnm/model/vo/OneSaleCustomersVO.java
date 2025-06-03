package com.jnm.mallJnm.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jnm.mallJnm.model.SalesCustomer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("jnm_sales_customer")
public class OneSaleCustomersVO extends SalesCustomer {
    private String name;
    private String account;
}

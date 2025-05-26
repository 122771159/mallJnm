package com.jnm.mallJnm.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jnm.mallJnm.model.Customer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("jnm_customer")
public class CustomerVO extends Customer {
    private String groupName;
}
package com.jnm.mallJnm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnm.mallJnm.model.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    // 可添加自定义查询方法
    // 示例：根据openid查询客户
    // Customer selectByOpenid(@Param("openid") String openid);
}
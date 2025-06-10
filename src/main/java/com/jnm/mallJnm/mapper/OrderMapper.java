package com.jnm.mallJnm.mapper;

import com.jnm.mallJnm.model.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends DefaultMapper<Order> {
    // 暂时不需要自定义SQL方法，后续可按需添加
}
package com.jnm.mallJnm.mapper;

import com.jnm.mallJnm.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends DefaultMapper<OrderItem> {
    // 同样，暂时不需要自定义SQL方法
}
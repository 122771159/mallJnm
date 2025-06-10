package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.OrderItem;

public interface OrderItemService extends IService<OrderItem> {
    // 订单项的服务，大部分操作由OrderService调用
}
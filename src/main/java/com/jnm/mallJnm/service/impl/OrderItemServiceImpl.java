package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.OrderItemMapper;
import com.jnm.mallJnm.model.OrderItem;
import com.jnm.mallJnm.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    // 当前阶段，订单项的所有操作都由 OrderServiceImpl 统一协调。
    // 因此，这里暂时不需要添加额外的自定义方法。
    // ServiceImpl 已经提供了强大的CRUD基础功能，如 saveBatch, list, getById 等。
}
package com.jnm.mallJnm.model.vo;

import com.jnm.mallJnm.model.Order;
import com.jnm.mallJnm.model.OrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderVO extends Order {
    private List<OrderItem> orderItems;
}
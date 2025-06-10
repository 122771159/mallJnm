package com.jnm.mallJnm.model.vo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jnm.mallJnm.model.Order;
import com.jnm.mallJnm.model.OrderItem;
import com.jnm.mallJnm.util.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderListVO extends Order {
    private String customerName;
    private String salesName;

    // 这个字段将从数据库接收JSON字符串
    private String orderItemsJson;

    // 这个字段不会在数据库中，我们通过它向前端暴露最终的List<OrderItem>
    private List<OrderItem> orderItems;

    public String getOrderItemsJson(){
        return "";
    }

    // 定义一个setter，当MyBatis设置orderItemsJson时，自动解析成List
    public void setOrderItemsJson(String json) {
        this.orderItemsJson = json;
        if (json != null && !json.isEmpty()) {
            // [修正] 调用新的fromJson方法，并直接传入TypeReference对象
            this.orderItems = JSONUtil.fromJson(json, new TypeReference<List<OrderItem>>() {});
        } else {
            this.orderItems = Collections.emptyList();
        }
    }
}
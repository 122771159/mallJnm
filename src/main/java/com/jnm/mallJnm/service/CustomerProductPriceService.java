package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.CustomerProductPrice;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerProductPriceService extends IService<CustomerProductPrice> {
    // 设置客户商品价格
    boolean setCustomerPrice(String customerId, Long productId, BigDecimal price);
    
    // 批量获取客户商品价格
    List<CustomerProductPrice> getCustomerPrices(String customerId, List<Long> productIds);
    
    // 获取客户所有专属价格商品
    List<CustomerProductPrice> getCustomerAllPrices(String customerId);
}
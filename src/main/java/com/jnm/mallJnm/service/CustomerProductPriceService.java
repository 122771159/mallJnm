package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.CustomerProductPrice;

public interface CustomerProductPriceService extends IService<CustomerProductPrice> {
   Boolean isCustomerProductPriceExisted(String customerId, String productId, String excludeId);
}
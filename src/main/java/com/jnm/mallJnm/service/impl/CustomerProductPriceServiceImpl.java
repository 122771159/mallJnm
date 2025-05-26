package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.CustomerProductPriceMapper;
import com.jnm.mallJnm.model.CustomerProductPrice;
import com.jnm.mallJnm.service.CustomerProductPriceService;
import org.springframework.stereotype.Service;


@Service
public class CustomerProductPriceServiceImpl 
    extends ServiceImpl<CustomerProductPriceMapper, CustomerProductPrice>
    implements CustomerProductPriceService {

    @Override
    public Boolean isCustomerProductPriceExisted(String customerId, String productId, String excludeId) {
        return null;
    }
}
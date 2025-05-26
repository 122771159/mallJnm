package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.CustomerProductPriceMapper;
import com.jnm.mallJnm.model.CustomerProductPrice;
import com.jnm.mallJnm.service.CustomerProductPriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerProductPriceServiceImpl 
    extends ServiceImpl<CustomerProductPriceMapper, CustomerProductPrice>
    implements CustomerProductPriceService {

    @Override
    public boolean setCustomerPrice(String customerId, Long productId, BigDecimal price) {
        CustomerProductPrice entity = new CustomerProductPrice();
        entity.setCustomerId(customerId);
        entity.setProductId(productId);
        entity.setCustomPrice(price);
        return saveOrUpdate(entity);
    }

    @Override
    public List<CustomerProductPrice> getCustomerPrices(String customerId, List<Long> productIds) {
        return lambdaQuery()
                .eq(CustomerProductPrice::getCustomerId, customerId)
                .in(CustomerProductPrice::getProductId, productIds)
                .list();
    }

    @Override
    public List<CustomerProductPrice> getCustomerAllPrices(String customerId) {
        return lambdaQuery()
                .eq(CustomerProductPrice::getCustomerId, customerId)
                .list();
    }
}
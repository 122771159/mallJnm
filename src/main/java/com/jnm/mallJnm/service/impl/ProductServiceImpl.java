package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.ProductMapper;
import com.jnm.mallJnm.model.Product;
import com.jnm.mallJnm.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl 
    extends ServiceImpl<ProductMapper, Product> 
    implements ProductService {

    @Override
    public List<Product> getPublishedProducts() {
        return lambdaQuery()
                .eq(Product::getIsPublished, true)
                .orderByAsc(Product::getSortOrder)
                .list();
    }

    @Override
    public boolean publishProduct(String productId) {
        return lambdaUpdate()
                .eq(Product::getId, productId)
                .set(Product::getIsPublished, true)
                .set(Product::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public boolean unpublishProduct(String productId) {
        return lambdaUpdate()
                .eq(Product::getId, productId)
                .set(Product::getIsPublished, false)
                .set(Product::getUpdateTime, LocalDateTime.now())
                .update();
    }
    @Override
    public boolean deductStock(String productId, Integer quantity) {
        return baseMapper.deductStock(productId, quantity) > 0;
    }
}
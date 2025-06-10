package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.Product;

import java.util.List;

public interface ProductService extends IService<Product> {
    // 自定义业务方法
    List<Product> getPublishedProducts(); // 示例：获取已上架商品
    boolean publishProduct(String productId); // 示例：商品上架
    boolean unpublishProduct(String productId); // 示例：商品下架
    /**
     * 扣减商品库存
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 是否扣减成功
     */
    boolean deductStock(String productId, Integer quantity);
}
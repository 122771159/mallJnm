package com.jnm.mallJnm.service.impl;

import com.jnm.mallJnm.model.CustomerProductPrice;
import com.jnm.mallJnm.model.GroupProductPrice;
import com.jnm.mallJnm.model.Product;
import com.jnm.mallJnm.model.vo.ProductDisplayVO;
import com.jnm.mallJnm.service.CustomerProductPriceService;
import com.jnm.mallJnm.service.GroupProductPriceService;
import com.jnm.mallJnm.service.PriceCalculationService;
import com.jnm.mallJnm.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PriceCalculationServiceImpl implements PriceCalculationService {

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerProductPriceService customerProductPriceService; //

    @Autowired
    private GroupProductPriceService groupProductPriceService; //

    @Override
    public Map<String, BigDecimal> calculateEffectivePrices(List<String> productIds, String customerId, String customerGroupId) {
        if (CollectionUtils.isEmpty(productIds)) {
            return new HashMap<>();
        }
        Map<String, BigDecimal> effectivePrices = new HashMap<>();
        // 1. 获取商品原价
        Map<String, Product> productsMap = productService.lambdaQuery()
                .in(Product::getId, productIds)
                .list()
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
        // 初始化为商品原价
        for (String productId : productIds) {
            Product product = productsMap.get(productId);
            if (product != null) {
                effectivePrices.put(productId, product.getPrice());
            }
        }
        // 2. 获取并应用客户组专属价格
        if (customerGroupId != null) {
            List<GroupProductPrice> groupPrices = groupProductPriceService.lambdaQuery()
                    .eq(GroupProductPrice::getGroupId, customerGroupId)
                    .in(GroupProductPrice::getProductId, productIds)
                    .list();
            for (GroupProductPrice gpPrice : groupPrices) {
                effectivePrices.put(gpPrice.getProductId(), gpPrice.getCustomPrice());
            }
        }

        // 3. 获取并应用客户专属价格 (最高优先级)
        if (customerId != null) {
            // CustomerProductPriceService 中 isCustomerProductPriceExisted 方法的实现需要调整以支持批量查询
            // 或者在这里进行批量查询
            List<CustomerProductPrice> customerPrices = customerProductPriceService.lambdaQuery()
                    .eq(CustomerProductPrice::getCustomerId, customerId)
                    .in(CustomerProductPrice::getProductId, productIds) // 注意 productId 类型是 Long
                    .list();

            Map<String, BigDecimal> customerPriceMap = customerPrices.stream()
                    .collect(Collectors.toMap(CustomerProductPrice::getProductId, CustomerProductPrice::getCustomPrice));

            for (String productIdStr : productIds) {
                try {
                    if (customerPriceMap.containsKey(productIdStr)) {
                        effectivePrices.put(productIdStr, customerPriceMap.get(productIdStr));
                    }
                } catch (NumberFormatException e) {
                    // 处理 productIdStr 不能转换为 Long 的情况，例如记录日志
                    System.err.println("Invalid productId format: " + productIdStr);
                }
            }
        }
        return effectivePrices;
    }

    @Override
    public ProductDisplayVO calculateEffectivePrice(String productId, String customerId, String customerGroupId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return null; // 或抛出异常
        }
        BigDecimal effectivePrice = product.getPrice(); // 默认为商品原价

        // 尝试获取客户组价格
        if (customerGroupId != null) {
            GroupProductPrice groupPrice = groupProductPriceService.lambdaQuery()
                    .eq(GroupProductPrice::getGroupId, customerGroupId)
                    .eq(GroupProductPrice::getProductId, productId)
                    .one();
            if (groupPrice != null) {
                effectivePrice = groupPrice.getCustomPrice();
            }
        }

        // 尝试获取客户专属价格 (最高优先级)
        if (customerId != null) {
            try {
                 Long productIdLong = Long.parseLong(productId); // ProductId 在Product表中是bigint
                 CustomerProductPrice customerPrice = customerProductPriceService.lambdaQuery()
                        .eq(CustomerProductPrice::getCustomerId, customerId)
                        .eq(CustomerProductPrice::getProductId, productIdLong) // ProductId 在 CustomerProductPrice 表中是 Long
                        .one();
                if (customerPrice != null) {
                    effectivePrice = customerPrice.getCustomPrice();
                }
            } catch (NumberFormatException e) {
                 System.err.println("Invalid productId format for customer price: " + productId);
            }

        }
        ProductDisplayVO displayVO = new ProductDisplayVO();
        BeanUtils.copyProperties(product, displayVO);
        displayVO.setDisplayPrice(effectivePrice);
        displayVO.setMainImage(product.getMainImage());
        return displayVO;
    }
}
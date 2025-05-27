package com.jnm.mallJnm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PriceCalculationService {
    /**
     * 计算一批商品的最终生效价格
     * @param productIds 商品ID列表
     * @param customerId 当前登录客户ID (可为null)
     * @param customerGroupId 当前客户所属的客户组ID (可为null)
     * @return Map<productId, effectivePrice>
     */
    Map<String, BigDecimal> calculateEffectivePrices(List<String> productIds, String customerId, String customerGroupId);

    /**
     * 计算单个商品的最终生效价格
     * @param productId 商品ID
     * @param customerId 当前登录客户ID (可为null)
     * @param customerGroupId 当前客户所属的客户组ID (可为null)
     * @return effectivePrice
     */
    BigDecimal calculateEffectivePrice(String productId, String customerId, String customerGroupId);
}
package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.GroupProductPrice;

public interface GroupProductPriceService extends IService<GroupProductPrice> {
    /**
     * 检查指定客户组的特定商品是否已经设置了专属价格
     * @param groupId 客户组ID
     * @param productId 商品ID
     * @param excludeId 如果是更新操作，可以排除当前的记录ID
     * @return true 如果已存在，false 如果不存在
     */
    boolean isGroupProductPriceExisted(String groupId, String productId, String excludeId);


}

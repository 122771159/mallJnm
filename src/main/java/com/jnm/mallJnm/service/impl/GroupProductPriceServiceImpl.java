package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.GroupProductPriceMapper;
import com.jnm.mallJnm.model.GroupProductPrice;
import com.jnm.mallJnm.service.GroupProductPriceService;
import org.springframework.stereotype.Service;

@Service
public class GroupProductPriceServiceImpl extends ServiceImpl<GroupProductPriceMapper, GroupProductPrice> implements GroupProductPriceService {

    @Override
    public boolean isGroupProductPriceExisted(String groupId, String productId, String excludeId) {
        LambdaQueryWrapper<GroupProductPrice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupProductPrice::getGroupId, groupId)
                .eq(GroupProductPrice::getProductId, productId);
        if (excludeId != null) {
            queryWrapper.ne(GroupProductPrice::getId, excludeId);
        }
        return baseMapper.exists(queryWrapper);
    }
}

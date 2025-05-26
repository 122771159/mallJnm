package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.CustomerGroupMapper;
import com.jnm.mallJnm.model.CustomerGroup;
import com.jnm.mallJnm.service.CustomerGroupService;
import org.springframework.stereotype.Service;

@Service
public class CustomerGroupServiceImpl extends ServiceImpl<CustomerGroupMapper, CustomerGroup> implements CustomerGroupService {
    @Override
    public boolean isGroupNameExisted(String groupName, String excludeGroupId) {
        LambdaQueryWrapper<CustomerGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerGroup::getName, groupName);
        if (excludeGroupId != null) {
            queryWrapper.ne(CustomerGroup::getId, excludeGroupId);
        }
        return baseMapper.exists(queryWrapper);
    }
}

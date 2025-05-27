package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.CustomerGroup;

public interface CustomerGroupService extends IService<CustomerGroup> {
    /**
     * 检查客户组名称是否已经存在
     * @param groupName 客户组名称
     * @param excludeGroupId 如果是更新操作，可以排除当前的组ID
     * @return true 如果已存在，false 如果不存在
     */
    boolean isGroupNameExisted(String groupName, String excludeGroupId);


}

package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.vo.ChangePasswordVO;

import java.util.List;

public interface CustomerService extends IService<Customer> {
    // 自定义业务方法
    boolean disableCustomer(String customerId);  // 禁用客户
    boolean enableCustomer(String customerId);   // 启用客户
    Customer getByAccount(String account);      // 根据账号获取客户
    void settingGroupBatch(List<String> ids, String group_id);
    void removeGroupBatch(List<String> ids,String group_id);
    Customer getByOpenId(String openId);
    boolean updateCustomer(ChangePasswordVO changePasswordVO);
}
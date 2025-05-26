package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.CustomerMapper;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.service.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl 
    extends ServiceImpl<CustomerMapper, Customer> 
    implements CustomerService {

    @Override
    public boolean disableCustomer(String customerId) {
        return lambdaUpdate()
                .eq(Customer::getId, customerId)
                .set(Customer::getStatus, 0)
                .update();
    }

    @Override
    public boolean enableCustomer(String customerId) {
        return lambdaUpdate()
                .eq(Customer::getId, customerId)
                .set(Customer::getStatus, 1)
                .update();
    }

    @Override
    public Customer getByAccount(String account) {
        return lambdaQuery()
                .eq(Customer::getAccount, account)
                .one();
    }
}
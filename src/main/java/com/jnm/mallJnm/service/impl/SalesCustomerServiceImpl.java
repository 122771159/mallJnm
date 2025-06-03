package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.exception.ValidatedException;
import com.jnm.mallJnm.mapper.SalesCustomerMapper;
import com.jnm.mallJnm.model.SalesCustomer;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.service.SalesCustomerService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesCustomerServiceImpl extends ServiceImpl<SalesCustomerMapper, SalesCustomer> implements SalesCustomerService {


    @Override
    public boolean isSaleCustomerExisted(String cid, String aid) {
        LambdaQueryWrapper<SalesCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalesCustomer::getAid, aid);
        queryWrapper.eq(SalesCustomer::getCid, cid);
        return baseMapper.exists(queryWrapper);
    }

    @Override
    public void addSaleCustomerBatch(List<String> cids, String aid) {
        if (CollectionUtils.isEmpty(cids) || StringUtil.isNullOrEmpty(aid)) {
            throw new ValidatedException(ErrorEnum.OBJECT_EMPTY);
        }
        LocalDateTime now = LocalDateTime.now();
        List<SalesCustomer> salesCustomers = new ArrayList<>();
        for (String cid : cids) {
            SalesCustomer salesCustomer = new SalesCustomer();
            salesCustomer.setCid(cid);
            salesCustomer.setAid(aid);
            salesCustomer.setCreateTime(now);
            salesCustomer.setUpdateTime(now);
            salesCustomers.add(salesCustomer);
        }
        baseMapper.insertList(salesCustomers);
    }
    @Transactional
    @Override
    public void removeSaleCustomerBatch(List<String> ids, String aid) {
        if (CollectionUtils.isEmpty(ids) || StringUtil.isNullOrEmpty(aid)) {
            throw new ValidatedException(ErrorEnum.OBJECT_EMPTY);
        }
        LambdaQueryWrapper<SalesCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalesCustomer::getAid, aid)
                .in(SalesCustomer::getId, ids);

        baseMapper.delete(queryWrapper);
    }
}

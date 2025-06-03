package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.SalesCustomer;

import java.util.List;

public interface SalesCustomerService extends IService<SalesCustomer> {

    /**
     * 检查单个关联客户是否已经存在
     * @param cid 客户id
     * @param aid 业务员id
     * @return true 如果已存在，false 如果不存在
     */
    boolean isSaleCustomerExisted(String cid, String aid);
    /**
     * 批量增加
     * @param cids 客户id 列表
     * @param aid 业务员id
     */
    void addSaleCustomerBatch(List<String> cids, String aid);
    /**
     * 批量删除
     * @param ids 关联表id
     * @param aid 业务员id
     */
    void removeSaleCustomerBatch(List<String> ids,String aid);

}

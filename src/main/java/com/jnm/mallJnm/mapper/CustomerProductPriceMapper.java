package com.jnm.mallJnm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnm.mallJnm.model.CustomerProductPrice;
import com.jnm.mallJnm.model.vo.ProductWithCustomerPriceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerProductPriceMapper extends BaseMapper<CustomerProductPrice> {
    // 自定义方法示例：批量查询客户商品价格
    // List<CustomerProductPrice> selectByCustomerAndProducts(
    //     @Param("customerId") String customerId,
    //     @Param("productIds") List<Long> productIds);
    /**
     * 获取带客户专属价格的商品列表
     * @param customerId 客户ID
     * @param productIds 商品ID列表(可选)
     */
    List<ProductWithCustomerPriceVO> selectProductsWithCustomerPrice(
            @Param("customerId") String customerId,
            @Param("productIds") List<Long> productIds);
}
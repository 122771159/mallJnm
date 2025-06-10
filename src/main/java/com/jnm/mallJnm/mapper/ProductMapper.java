package com.jnm.mallJnm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnm.mallJnm.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 安全地扣减库存 (原子操作)
     * 通过在WHERE子句中检查库存，防止超卖
     * @return 受影响的行数，0表示扣减失败（库存不足）
     */
    @Update("UPDATE jnm_product SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int deductStock(@Param("productId") String productId, @Param("quantity") Integer quantity);
}
package com.jnm.mallJnm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnm.mallJnm.model.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 可添加自定义查询方法（例如联表查询）
    // 示例：按分类查询商品
    // List<Product> selectByCategoryId(@Param("categoryId") Long categoryId);
}
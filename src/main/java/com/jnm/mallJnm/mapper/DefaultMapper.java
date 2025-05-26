package com.jnm.mallJnm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DefaultMapper<T> extends BaseMapper<T> {
    /**
     * 如果要自动填充，@{@code Param}(xx) xx参数名必须是 list/collection/array 3个的其中之一
     *
     * @param list 批量添加的对象列表
     * @return 添加成功的条数
     */
    int insertList(@Param("list") List<T> list);
}

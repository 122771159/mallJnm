package com.jnm.mallJnm.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jnm.mallJnm.mybatisplus.wrapper.JoinWrapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface AbstractJoinMapper<T> extends Mapper<T> {
    List<T> selectJoinList(@Param("ew") JoinWrapper<T> joinWrapper);

    <E extends IPage<T>> E selectJoinPage(E page, @Param("ew") JoinWrapper<T> joinWrapper);

    Long selectJoinCount(@Param("ew") JoinWrapper<T> joinWrapper);
}

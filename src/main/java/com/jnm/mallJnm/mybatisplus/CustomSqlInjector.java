package com.jnm.mallJnm.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.jnm.mallJnm.mybatisplus.method.InsertList;
import com.jnm.mallJnm.mybatisplus.method.SelectJoinCount;
import com.jnm.mallJnm.mybatisplus.method.SelectJoinList;
import com.jnm.mallJnm.mybatisplus.method.SelectJoinPage;
import org.apache.ibatis.session.Configuration;

import java.util.List;

public class CustomSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        methodList.add(new InsertList());
        methodList.add(new SelectJoinList());
        methodList.add(new SelectJoinPage());
        methodList.add(new SelectJoinCount());
        return methodList;
    }
}

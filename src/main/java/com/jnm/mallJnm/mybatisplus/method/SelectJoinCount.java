package com.jnm.mallJnm.mybatisplus.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;


public class SelectJoinCount extends AbstractJoinMethod{
    public SelectJoinCount() {
        this("selectJoinCount");
    }

    public SelectJoinCount(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = "<script>%s SELECT COUNT(%s) FROM %s %s %s %s %s\n</script>";
        String sqlResult = String.format(sql, this.sqlFirst(), this.sqlCount(), tableInfo.getTableName(),
                "${ew.alias}", sqlFrom(), this.sqlWhereEntityWrapper(true, tableInfo), this.sqlComment());
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sqlResult, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, "selectJoinCount", sqlSource, Long.class);
    }
}

package com.jnm.mallJnm.mybatisplus.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;


public class SelectJoinList extends AbstractJoinMethod{
    public SelectJoinList() {
        this("selectJoinList");
    }

    public SelectJoinList(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = "<script>%s SELECT %s FROM %s %s %s %s %s\n</script>";
        String sqlResult = String.format(sql, this.sqlFirst(), this.sqlSelectColumns(tableInfo),
                tableInfo.getTableName(), "${ew.alias}", sqlFrom(), this.sqlWhereEntityWrapper(true, tableInfo),
                this.sqlComment());
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sqlResult, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, "selectJoinList", sqlSource, modelClass);
    }
}

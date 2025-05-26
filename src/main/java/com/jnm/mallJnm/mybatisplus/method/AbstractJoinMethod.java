package com.jnm.mallJnm.mybatisplus.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;


public abstract class AbstractJoinMethod extends AbstractMethod {
    public AbstractJoinMethod(String methodName) {
        super(methodName);
    }

    protected String sqlSelectColumns(TableInfo table) {
        String selectColumns = "*";
        if (table.getResultMap() == null || table.isAutoInitResultMap()) {
            selectColumns = table.getAllSqlSelect();
            String[] columns = selectColumns.split(",");
            StringBuilder sqlColumns = new StringBuilder();
            for (String column : columns) {
                sqlColumns.append(",").append("${ew.alias}").append(".").append(column);
            }
            selectColumns = sqlColumns.substring(1);
        }

        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null", "ew", "ew.sqlSelect"), SqlScriptUtils.unSafeParam("ew.sqlSelect"), selectColumns);
    }

    protected String sqlFrom() {
        String condition = String.format("%s != null and %s != ''", "ew.from", "ew.from");
        return SqlScriptUtils.convertIf("${ew.from}", condition, false);
    }
}

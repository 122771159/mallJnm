package com.jnm.mallJnm.mybatisplus.wrapper;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;


public class JoinWrapper<T> extends AbstractWrapper<T, String, JoinWrapper<T>> implements Query<JoinWrapper<T>, T, String>, Join<JoinWrapper<T>> {
    private final SharedString sqlSelect;
    private String alias = "t";
    private final SharedString from;

    public JoinWrapper() {
        this(null);
    }

    public JoinWrapper(T entity) {
        this.sqlSelect = new SharedString();
        this.from = SharedString.emptyString();
        super.setEntity(entity);
        super.initNeed();
    }

    public JoinWrapper(T entity, String... columns) {
        this.sqlSelect = new SharedString();
        this.from = SharedString.emptyString();
        super.setEntity(entity);
        super.initNeed();
        this.select(columns);
    }

    public JoinWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
                       Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                       SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        this.sqlSelect = new SharedString();
        this.from = new SharedString();
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    @Override
    protected JoinWrapper<T> instance() {
        return new JoinWrapper<>(this.getEntity(), this.getEntityClass(), this.paramNameSeq, this.paramNameValuePairs, new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public JoinWrapper<T> select(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(String.join(",", columns));
        }
        return this.typedThis;
    }

    @Override
    public JoinWrapper<T> select(boolean condition, List<String> columns) {
        if (condition && CollectionUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(String.join(",", columns));
        }
        return this.typedThis;
    }

    @Override
    public JoinWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        super.setEntityClass(entityClass);
        this.sqlSelect.setStringValue(TableInfoHelper.getTableInfo(this.getEntityClass()).chooseSelect(predicate));
        return this.typedThis;
    }

    @Override
    public JoinWrapper<T> join(String keyWord, boolean condition, String joinSql) {
        if (condition) {
            this.from.setStringValue(this.from.getStringValue() + keyWord + joinSql);
        }
        return typedThis;
    }

    @Override
    public void clear() {
        super.clear();
        this.sqlSelect.toNull();
        this.from.toNull();
    }

    @Override
    public String getSqlSelect() {
        return this.sqlSelect.getStringValue();
    }

    public final JoinWrapper<T> alias(String tableAlias) {
        this.alias = tableAlias;
        return this.typedThis;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getFrom() {
        return this.from.getStringValue();
    }
}

package com.jnm.mallJnm.mybatisplus.wrapper;


public interface Join<Children> {
    default Children leftJoin(String joinSql) {
        return leftJoin(true, joinSql);
    }

    default Children leftJoin(boolean condition, String joinSql) {
        return join(" left join ", condition, joinSql);
    }

    default Children rightJoin(String joinSql) {
        return rightJoin(true, joinSql);
    }

    default Children rightJoin(boolean condition, String joinSql) {
        return join(" right join ", condition, joinSql);
    }

    default Children innerJoin(String joinSql) {
        return innerJoin(true, joinSql);
    }

    default Children innerJoin(boolean condition, String joinSql) {
        return join(" inner join ", condition, joinSql);
    }

    Children join(String keyWord, boolean condition, String joinSql);
}

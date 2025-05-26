CREATE TABLE `jnm_admin`
(
    `id`          varchar(64)  NOT NULL COMMENT '用户ID',
    `username`    varchar(100) NOT NULL COMMENT '用户名',
    `password`    varchar(255) DEFAULT NULL COMMENT '密码',
    `user_type`   varchar(20)  NOT NULL COMMENT '用户类型',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email_user_type` (`email`, `user_type`) COMMENT '邮箱和用户类型联合唯一约束'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

CREATE TABLE `jnm_product_category`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        varchar(50) NOT NULL COMMENT '分类名称',
    `sort_order`  int(11) DEFAULT 0 COMMENT '排序权重，数字越大越靠前',
    `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
    `is_show`     tinyint(1) DEFAULT 1 COMMENT '是否显示(0:不显示 1:显示)',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_sort_order` (`sort_order`),
    KEY           `idx_is_show` (`is_show`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE `jnm_product`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `category_id`  int(11) NOT NULL COMMENT '分类ID',
    `name`         varchar(100)   NOT NULL COMMENT '商品名称',
    `code`         varchar(50)             DEFAULT NULL COMMENT '商品编码',
    `description`  text COMMENT '商品详情描述',
    `images`       varchar(2000)           DEFAULT NULL COMMENT '图片URL集合，多个用逗号分隔，第一个为主图',
    `price`        decimal(10, 2) NOT NULL COMMENT '商品价格',
    `cost_price`   decimal(10, 2)          DEFAULT NULL COMMENT '成本价',
    `stock`        int(11) NOT NULL DEFAULT '0' COMMENT '库存',
    `sales`        int(11) DEFAULT '0' COMMENT '销量',
    `is_published` tinyint(1) DEFAULT '0' COMMENT '是否上架(0:否 1:是)',
    `sort_order`   int(11) DEFAULT '0' COMMENT '排序权重',
    `create_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY            `idx_category_id` (`category_id`),
    KEY            `idx_is_published` (`is_published`),
    KEY            `idx_sort_order` (`sort_order`),
    KEY            `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
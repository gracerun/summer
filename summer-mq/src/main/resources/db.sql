CREATE TABLE `business_msg`
(
    `id`                  bigint                                                NOT NULL COMMENT '主键',
    `optimistic`          int                                                   NOT NULL DEFAULT '0' COMMENT '乐观锁',
    `status`              varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '状态',
    `create_time`         datetime                                              NOT NULL COMMENT '创建时间',
    `last_update_time`    datetime                                              NOT NULL COMMENT '最新更新时间',
    `business_no`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '业务编号',
    `namespace`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '名称空间',
    `business_type`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '业务类型',
    `times`               int                                                   NOT NULL DEFAULT '0' COMMENT '执行次数',
    `content`             varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin        DEFAULT NULL COMMENT '内容',
    `remark`              varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT '备注',
    `next_execute_time`   datetime                                              NOT NULL COMMENT '下次执行时间',
    `time`                int                                                            DEFAULT NULL COMMENT '执行耗时',
    `ip`                  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT 'ip地址',
    `create_span_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '数据创建时的span_id',
    `last_update_span_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '数据最后一次更新时的span_id',
    `timeout`             int                                                            DEFAULT NULL COMMENT '执行超时时间（单位秒）',
    PRIMARY KEY (`id`),
    KEY `idx_business_no` (`business_no`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_exetime` (`next_execute_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='业务消息'
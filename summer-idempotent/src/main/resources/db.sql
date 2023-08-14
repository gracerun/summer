CREATE TABLE `idempotent_data`
(
    `create_time`  datetime    NOT NULL COMMENT '创建时间',
    `primary_no`   varchar(64) NOT NULL COMMENT '业务编号',
    `primary_type` varchar(64) NOT NULL COMMENT '业务类型',
    PRIMARY KEY (`primary_no`, `primary_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='幂等校验';
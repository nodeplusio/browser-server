ALTER TABLE token
    ALTER COLUMN `is_support_erc1155` SET DEFAULT '0';
ALTER TABLE token
    ALTER COLUMN `is_support_erc1155_metadata` SET DEFAULT '0';

DROP TABLE IF EXISTS `node_history_total_and_stat_delegate_value`;
CREATE TABLE `node_history_total_and_stat_delegate_value`
(
    `node_id`                   varchar(130)   NOT NULL COMMENT '节点id',
    `date`                      datetime       NOT NULL COMMENT '日期',
    `stat_delegate_value_max`   decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总委托 最大值	(von)',
    `stat_delegate_value_min`   decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总委托 最小值	(von)',
    `stat_delegate_value_avg`   decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总委托 平均值	(von)',
    `stat_delegate_value_total` decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总委托值	(von)',
    `stat_delegate_value_count` int            NOT NULL DEFAULT '0' COMMENT '总委托数',
    `stat_staking_value_max`    decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总质押 最大值	(von)',
    `stat_staking_value_min`    decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总质押 最小值	(von)',
    `stat_staking_value_avg`    decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总质押 平均值	(von)',
    `stat_staking_value_total`  decimal(65, 0) NOT NULL DEFAULT '0' COMMENT '总质押值	(von)',
    `stat_staking_value_count`  int            NOT NULL DEFAULT '0' COMMENT '总质押数',
    `create_time`               timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`               timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`node_id`, `date`)
);

DROP TABLE IF EXISTS `node_history_dele_annualized_rate`;
CREATE TABLE `node_history_dele_annualized_rate`
(
    `node_id`                  varchar(130)    NOT NULL COMMENT '节点id',
    `date`                     datetime        not NULL COMMENT '日期',
    `dele_annualized_rate_max` decimal(65, 18) NOT NULL DEFAULT '0' COMMENT '年化率 最大值',
    `dele_annualized_rate_min` decimal(65, 18) NOT NULL DEFAULT '0' COMMENT '年化率 最小值',
    `dele_annualized_rate_avg` decimal(65, 18) NOT NULL DEFAULT '0' COMMENT '年化率 平均值',
    `dele_reward`              decimal(65, 18) NOT NULL DEFAULT '0' COMMENT '奖励',
    `create_time`              timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`              timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`node_id`, `date`)
);

DROP TABLE IF EXISTS `delegation_log`;
CREATE TABLE `delegation_log`
(
    `id`          bigint(20) NOT NULL COMMENT 'id',
    `node_id`     varchar(130) NOT NULL COMMENT '节点id',
    `time`        timestamp NULL DEFAULT NULL COMMENT '交易时间',
    `hash`        varchar(72)  NOT NULL COMMENT '交易hash',
    `type`        int(10) DEFAULT NULL COMMENT '交易类型',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    index (`node_id`, `time`)
);

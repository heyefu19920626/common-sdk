DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`
(
    `id`          int      NOT NULL AUTO_INCREMENT COMMENT '任务id',
    `name`        varchar(25)       DEFAULT '' COMMENT '任务名称',
    `module`      varchar(25)       DEFAULT NULL COMMENT '任务所属模块',
    `status`      varchar(10)       DEFAULT 'CREATE' COMMENT '任务状态',
    `result`      varchar(255)      DEFAULT NULL COMMENT  '任务结果相关的信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
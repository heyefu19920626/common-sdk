# 创建表
create table task
(
    `id`          int(11) auto_increment comment '任务id',
    `name`        varchar(25) default '' comment '任务名称',
    `status`      varchar(10) default 'CREATE' comment '任务状态',
    `create_time` datetime    default null comment '创建时间',
    `update_time` datetime not null on update current_timestamp,
    primary key (`id`)
);

# 修改列
alter table task
    MODIFY COLUMN `update_time` datetime NOT NULL DEFAULT current_timestamp ON UPDATE CURRENT_TIMESTAMP AFTER `create_time`;

# 修改列
alter table task
    MODIFY COLUMN `create_time` datetime NOT NULL DEFAULT current_timestamp after `status`;
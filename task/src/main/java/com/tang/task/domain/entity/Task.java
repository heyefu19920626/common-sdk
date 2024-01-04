package com.tang.task.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 任务实例
 *
 * @author he
 * @since 2024-01.02-23:47
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    /**
     * 任务id
     */
    private int id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务状态
     */
    private TaskStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 需要运行的具体任务
     */
    private Runnable runnable;
}
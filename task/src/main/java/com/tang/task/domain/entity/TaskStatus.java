package com.tang.task.domain.entity;

/**
 * 任务状态
 *
 * @author he
 * @since 2024-01.02-23:51
 */
public enum TaskStatus {
    /**
     * 已创建
     */
    CREATE,
    /**
     * 等待执行
     */
    WAIT,
    /**
     * 执行中
     */
    RUNNING,
    /**
     * 运行成功
     */
    SUCCESS,
    /**
     * 运行失败
     */
    FAIL,
    /**
     * 已取消
     */
    ABORT;
}
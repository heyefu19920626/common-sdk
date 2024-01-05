package com.tang.task.domain.entity;

/**
 * 任务需要的操作
 *
 * @author he
 * @since 2024-01.05-22:36
 */
public interface ITaskOperate extends Runnable {
    /**
     * 设置任务id
     * <p>
     * 在任务操作中经常需要更新任务状态与进度信息，因此通过回调设置任务id
     *
     * @param taskId 设置任务id
     */
    void setTaskId(String taskId);
}
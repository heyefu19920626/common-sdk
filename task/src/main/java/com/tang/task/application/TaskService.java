package com.tang.task.application;

import com.tang.task.domain.entity.Task;
import com.tang.task.domain.entity.TaskStatus;
import com.tang.task.domain.exception.TaskErrorCode;
import com.tang.task.domain.exception.TaskException;
import com.tang.task.domain.mapper.TaskMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务服务
 *
 * @author he
 * @since 2024-01.05-22:48
 */
@Slf4j
@Service
@AllArgsConstructor
public class TaskService {
    private TaskMapper taskMapper;

    /**
     * 创建并使用虚拟线程启动任务
     *
     * @param task 任务参数
     * @return 任务，包含数据库生成的任务id
     */
    public Task createAndStartTask(Task task) {
        checkTask(task);
        task.setStatus(TaskStatus.CREATE);
        taskMapper.addTask(task);
        Thread.startVirtualThread(task.getTaskOperate());
        log.info("start task {} success.", task.getId());
        return task;
    }

    private static void checkTask(Task task) {
        if (task == null) {
            throw new TaskException(TaskErrorCode.CREATE_ERROR);
        }
        if (task.getModule() == null) {
            throw new TaskException(TaskErrorCode.CREATE_MODULE_NOT_NULL);
        }
        if (StringUtils.isBlank(task.getName())) {
            throw new TaskException(TaskErrorCode.CREATE_MODULE_NOT_NULL);
        }
    }

    /**
     * 查询任务
     *
     * @param task 需要查询的参数
     * @return 查询结果
     */
    public List<Task> queryTask(Task task) {
        return taskMapper.queryAllTask(task);
    }
}
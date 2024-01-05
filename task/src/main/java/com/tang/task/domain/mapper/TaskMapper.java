package com.tang.task.domain.mapper;

import com.tang.task.domain.entity.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 任务数据库映射
 *
 * @author he
 * @since 2024-01.03-00:00
 */
@Mapper
public interface TaskMapper {
    /**
     * 添加任务
     *
     * @param task 任务
     */
    void addTask(Task task);

    /**
     * 查询所有任务
     *
     * @param task 查询参数
     * @return 所有任务
     */
    List<Task> queryAllTask(Task task);
}
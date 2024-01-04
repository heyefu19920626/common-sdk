package com.tang.task.domain.mapper;

import com.tang.task.domain.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务数据库映射
 *
 * @author he
 * @since 2024-01.03-00:00
 */
@Mapper
public interface TaskMapper {
    void addTask(Task task);
}

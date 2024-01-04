package com.tang.task.domain.mapper;

import com.tang.task.domain.entity.Task;
import com.tang.task.domain.entity.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * mapper测试
 *
 * @author he
 * @since 2024-01.04-23:29
 */
@SpringBootTest
class TaskMapperTest {
    @Autowired
    TaskMapper mapper;

    @Test
    @DisplayName("添加任务成功后, task应该包含数据库自动增长的任务id")
    void should_contain_id_when_add_task_success() {
        Task task = Task.builder().name("test").status(TaskStatus.CREATE).build();
        mapper.addTask(task);
        Assertions.assertNotEquals(0, task.getId());
    }
}
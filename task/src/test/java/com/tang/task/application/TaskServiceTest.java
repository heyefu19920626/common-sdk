package com.tang.task.application;

import com.tang.base.context.Module;
import com.tang.task.domain.entity.ITaskOperate;
import com.tang.task.domain.entity.Task;
import com.tang.task.domain.exception.TaskErrorCode;
import com.tang.task.domain.exception.TaskException;
import com.tang.task.domain.mapper.TaskMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * 任务相关的服务测试
 *
 * @author he
 * @since 2024-01.05-23:16
 */
@SpringBootTest
class TaskServiceTest {
    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    @DisplayName("当查询参数为null，创建失败")
    void should_throw_create_fail_when_task_null() {
        TaskException exception = Assertions.assertThrows(TaskException.class,
            () -> taskService.createAndStartTask(null));
        Assertions.assertEquals(TaskErrorCode.CREATE_ERROR, exception.getErrorCode());
    }

    @Test
    @DisplayName("创建并启动任务成功")
    void should() {
        Task task = Task.builder().name("测试任务").module(Module.TASK).taskOperate(new ITaskOperate() {
            @Override
            public void setTaskId(String taskId) {
            }

            @Override
            public void run() {

            }
        }).build();
        task.setId(1);
        Mockito.doNothing().when(taskMapper).addTask(task);
        task = taskService.createAndStartTask(task);
        Assertions.assertEquals(1, task.getId());
    }
}
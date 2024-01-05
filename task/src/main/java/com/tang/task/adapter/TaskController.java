package com.tang.task.adapter;

import com.tang.base.response.Response;
import com.tang.task.application.TaskService;
import com.tang.task.domain.entity.Task;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务相关的接口
 *
 * @author he
 * @since 2024-01.05-22:49
 */
@RestController
@RequestMapping("task")
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;

    /**
     * 查询任务
     *
     * @param task 查询参数，当参数为空时，查询所有结果
     * @return 结果
     */
    @GetMapping
    public Response<List<Task>> queryTask(@RequestBody Task task) {
        return Response.success(taskService.queryTask(task));
    }
}
package com.tang.task.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tang.base.context.Module;
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
     * 任务名称, 长度需要小于25
     */
    private String name;

    /**
     * 任务所属模块
     */
    private Module module;

    /**
     * 任务状态
     */
    private TaskStatus status;

    /**
     * 任务结果
     * <p>
     * 有的任务会产生结果文件或信息
     */
    private String result;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 需要运行的具体任务
     */
    @JsonIgnore
    private ITaskOperate taskOperate;
}
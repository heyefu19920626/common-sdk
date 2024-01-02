package com.tang.task.domain.exception;

import com.tang.base.context.Module;
import com.tang.base.exception.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务相关的错误码
 *
 * @author he
 * @since 2024-01.02-23:54
 */
@AllArgsConstructor
@Getter
public enum TaskErrorCode implements IErrorCode {
    CREATE_ERROR("0001", "task.create.error");

    private final String moduleErrorCode;

    private final String descKey;


    @Override
    public Module getModule() {
        return Module.TASK;
    }
}

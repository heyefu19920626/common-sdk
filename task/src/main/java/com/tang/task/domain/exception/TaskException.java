package com.tang.task.domain.exception;

import com.tang.base.exception.BaseException;

/**
 * 任务相关的异常
 *
 * @author he
 * @since 2024-01.02-23:53
 */
public class TaskException extends BaseException {
    public TaskException(TaskErrorCode errorCode) {
        super(errorCode);
    }
}

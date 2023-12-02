/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.exception;

import com.tang.response.Response;

/**
 * Rest异常处理器接口
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
public interface IRestExceptionHandler {
    /**
     * 该实现类是否能够处理此异常
     *
     * @param throwable 异常
     * @return 是否能够处理
     */
    boolean canHandle(Throwable throwable);

    /**
     * 处理异常
     *
     * @param throwable 异常
     * @return 返回给前端的响应
     */
    Response<?> handle(Throwable throwable);
}
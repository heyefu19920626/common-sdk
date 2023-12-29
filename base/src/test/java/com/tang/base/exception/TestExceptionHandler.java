/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.exception;

import com.tang.base.response.Response;

/**
 * 测试异常处理器
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
public class TestExceptionHandler implements IRestExceptionHandler {
    @Override
    public boolean canHandle(Throwable throwable) {
        return TestException.class.isAssignableFrom(throwable.getClass());
    }

    @Override
    public Response<?> handle(Throwable throwable) {
        return Response.fail(new TestException(TestErrorCode.TEST_ERROR_CODE_2));
    }
}
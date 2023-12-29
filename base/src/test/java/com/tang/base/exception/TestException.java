/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.exception;

/**
 * 测试异常
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
public class TestException extends BaseException {
    public TestException(TestErrorCode errorCode) {
        super(errorCode);
    }
}
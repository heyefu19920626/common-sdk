/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.exception;

import com.tang.base.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

/**
 * 全局异常处理器测试
 */
@SpringBootTest
class GlobalExceptionHandlerTest {
    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("自定义的异常，应该返回自定义的错误描述")
    void should_return_error_2_when_use_custom_define_handle() {
        Response<?> response = globalExceptionHandler.handleThrowable(new TestException(1001, "test.error", null));
        Assertions.assertEquals("test.error.2", response.getDesc());
    }

    @Test
    @DisplayName("默认的异常，应该返回异常里的错误描述")
    void should_return_error_when_use_default_handle() {
        Response<?> response = globalExceptionHandler.handleThrowable(new BaseException(1001, "test.error", null));
        Assertions.assertEquals("test.error", response.getDesc());
    }

    @Test
    @DisplayName("未包装的异常，应该返回系统内部错误")
    void should_return_internal_error_when_not_define() {
        Response<?> response = globalExceptionHandler.handleThrowable(new Exception("test.error"));
        if (Locale.getDefault().equals(Locale.ENGLISH)) {
            Assertions.assertEquals("Internal System Error.", response.getDesc());
        } else {
            Assertions.assertEquals("系统内部错误。", response.getDesc());
        }
    }
}
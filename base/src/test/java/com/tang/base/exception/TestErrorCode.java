package com.tang.base.exception;

import com.tang.base.context.Module;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试的错误码
 *
 * @author he
 * @since 2023-12.30-00:44
 */
@Getter
@AllArgsConstructor
public enum TestErrorCode implements IErrorCode {
    TEST_ERROR_CODE_2("0002", "test.error.2"),
    TEST_ERROR_CODE("0001", "test.error");

    private final String moduleErrorCode;

    private final String descKey;

    @Override
    public Module getModule() {
        return Module.BASE;
    }
}
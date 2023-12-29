/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.exception;

import com.tang.base.context.Module;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 基础模块错误码
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Getter
@AllArgsConstructor
public enum BaseErrorCode implements IErrorCode {
    COMPILER_ERROR( "0004", "base.compiler.error"),
    FILE_SUFFIX_ILLEGAL( "0003", "base.file.suffix.illegal"),
    FILE_FORMAT_ILLEGAL( "0002", "base.file.format.illegal"),
    SYSTEM_INTERNAL_ERROR( "0001", "base.system.internal.error");

    private final String moduleErrorCode;
    private final String descKey;

    @Override
    public Module getModule() {
        return Module.BASE;
    }
}
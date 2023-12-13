/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.exception;

import com.tang.context.Module;
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
    COMPILER_ERROR(IErrorCode.initCode(Module.BASE, "0004"), "base.compiler.error", null, null),
    FILE_SUFFIX_ILLEGAL(IErrorCode.initCode(Module.BASE, "0003"), "base.file.suffix.illegal", null, null),
    FILE_FORMAT_ILLEGAL(IErrorCode.initCode(Module.BASE, "0002"), "base.file.format.illegal", null, null),
    SYSTEM_INTERNAL_ERROR(IErrorCode.initCode(Module.BASE, "0001"), "base.system.internal.error", null, null);

    private final int code;
    private final String descKey;
    private final String[] descParams;
    private final String[] suggestionParams;
}
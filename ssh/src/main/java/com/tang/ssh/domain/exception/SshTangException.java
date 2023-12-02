/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.exception;

import com.tang.exception.BaseException;

/**
 * 本项目相关的ssh异常
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
public class SshTangException extends BaseException {
    public SshTangException(SshErrorCode errorCode) {
        super(errorCode);
    }

    public SshTangException(int code, String descKey, String[] descParams, String... suggestionParams) {
        super(code, descKey, descParams, suggestionParams);
    }
}
/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.exception;

import com.tang.base.exception.BaseException;

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

    public SshTangException(SshErrorCode errorCode, String... descParams) {
        super(errorCode, descParams);
    }
}
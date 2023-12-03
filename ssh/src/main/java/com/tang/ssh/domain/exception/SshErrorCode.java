/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.exception;

import com.tang.context.Module;
import com.tang.exception.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ssh模块错误码
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Getter
@AllArgsConstructor
public enum SshErrorCode implements IErrorCode {
    SSH_CONN_HAVE_CLOSE(IErrorCode.initCode(Module.SSH, "0006"), "ssh.conn.has.closed", null, null),
    CHANNEL_HAVE_CLOSED(IErrorCode.initCode(Module.SSH, "0005"), "ssh.channel.has.closed", null, null),
    CREATE_CHANNEL_ERROR(IErrorCode.initCode(Module.SSH, "0004"), "ssh.create.channel.error", null, null),
    SEND_COMMAND_ERROR(IErrorCode.initCode(Module.SSH, "0003"), "ssh.send.command.error", null, null),
    CREATE_SESSION_ERROR(IErrorCode.initCode(Module.SSH, "0002"), "ssh.create.session.error", null, null),
    PARAM_ERROR(IErrorCode.initCode(Module.SSH, "0001"), "ssh.param.error", null, null);
    private final int code;
    private final String descKey;
    private final String[] descParams;
    private final String[] suggestionParams;
}
/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.exception;

import com.tang.base.context.Module;
import com.tang.base.exception.IErrorCode;

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
    SFTP_HAS_CLOSE(IErrorCode.initCode(Module.SSH, "0013"), "ssh.sftp.has.close", null, null),
    SFTP_DOWNLOAD_FAIL(IErrorCode.initCode(Module.SSH, "0012"), "ssh.sftp.download.fail", null, null),
    SFTP_UPLOAD_FAIL(IErrorCode.initCode(Module.SSH, "0011"), "ssh.sftp.upload.fail", null, null),
    CRETE_SFTP_FAIL(IErrorCode.initCode(Module.SSH, "0010"), "ssh.create.sftp.fail", null, null),
    HOST_CONNECT_FAIL(IErrorCode.initCode(Module.SSH, "0009"), "ssh.host.connect.fail", null, null),
    SERVER_AUTHENTICATION_FAIL(IErrorCode.initCode(Module.SSH, "0008"), "ssh.server.authentication.fail", null, null),
    SERVER_ALGORITHMS_UN_SUPPORT(IErrorCode.initCode(Module.SSH, "0007"), "ssh.server.algorithms.un.support", null,
        null),
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
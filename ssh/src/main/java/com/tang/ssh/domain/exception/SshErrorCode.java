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
    SFTP_HAS_CLOSE("0013", "ssh.sftp.has.close"),
    SFTP_DOWNLOAD_FAIL("0012", "ssh.sftp.download.fail"),
    SFTP_UPLOAD_FAIL("0011", "ssh.sftp.upload.fail"),
    CRETE_SFTP_FAIL("0010", "ssh.create.sftp.fail"),
    HOST_CONNECT_FAIL("0009", "ssh.host.connect.fail"),
    SERVER_AUTHENTICATION_FAIL("0008", "ssh.server.authentication.fail"),
    SERVER_ALGORITHMS_UN_SUPPORT("0007", "ssh.server.algorithms.un.support"),
    SSH_CONN_HAVE_CLOSE("0006", "ssh.conn.has.closed"),
    CHANNEL_HAVE_CLOSED("0005", "ssh.channel.has.closed"),
    CREATE_CHANNEL_ERROR("0004", "ssh.create.channel.error"),
    SEND_COMMAND_ERROR("0003", "ssh.send.command.error"),
    CREATE_SESSION_ERROR("0002", "ssh.create.session.error"),
    PARAM_ERROR("0001", "ssh.param.error");

    private final String moduleErrorCode;

    private final String descKey;


    @Override
    public Module getModule() {
        return Module.SSH;
    }
}
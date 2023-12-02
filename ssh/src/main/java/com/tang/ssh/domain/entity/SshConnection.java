/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.utils.CloseUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;

import java.io.Closeable;
import java.io.IOException;

/**
 * ssh连接
 * <p>
 * 1、可以执行交互式命令
 * 2、同步执行命令
 * 3、能够支持Ctrl+C等按键
 * 4、自动重连
 * <p>
 * todo 命令超时机制，top等命令的支持, 重连
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
@Builder
public class SshConnection implements Closeable {
    private SshClient client;

    private ClientSession session;

    private SshParam sshParam;

    private SshMonitor monitor;

    /**
     * 发送命令
     *
     * @param command 需要发送的命令
     * @return 命令的响应
     * @throws SshTangException 发送失败异常
     */
    public String sendCommand(String command) throws SshTangException {
        return sendCommand(command, true, true);
    }

    /**
     * 发送命令, 不打印命令本身
     *
     * @param command 需要发送的命令
     * @return 命令的响应
     * @throws SshTangException 发送失败异常
     */
    public String sendCommandWithoutCommandLog(String command) throws SshTangException {
        return sendCommand(command, false, true);
    }

    /**
     * 发送命令
     *
     * @param command    需要发送的命令
     * @param logCommand 是否打印命令本身
     * @param logEcho    是否打印回显
     * @return 命令执行结果
     * @throws SshTangException 发送失败异常
     */
    public String sendCommand(String command, boolean logCommand, boolean logEcho) throws SshTangException {
        checkConnect();
        if (logCommand) {
            log.info("start send command: {}", command);
        }
        try {
            String result = monitor.sendCommand(command);
            if (logEcho) {
                log.info("receive result: \n{}", result);
            }
            return result;
        } catch (SshTangException e) {
            log.error("send command error.", e);
            throw new SshTangException(SshErrorCode.SEND_COMMAND_ERROR);
        }
    }

    private void checkConnect() throws SshTangException {
        if (client == null || session == null) {
            throw new SshTangException(SshErrorCode.CREATE_SESSION_ERROR);
        }
    }

    @Override
    public void close() throws IOException {
        CloseUtils.close(session, client);
    }
}
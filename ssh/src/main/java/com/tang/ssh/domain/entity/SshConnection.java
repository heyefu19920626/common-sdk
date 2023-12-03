/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.utils.CloseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.session.ClientSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
public class SshConnection implements Closeable {
    private final SshParam sshParam;

    private SshClient client;

    private ClientSession session;

    private ChannelShell channelShell;

    private SshMonitor monitor;

    public SshConnection(SshParam sshParam, SshClient client, ClientSession session) throws SshTangException {
        this.client = client;
        this.session = session;
        this.sshParam = sshParam;
        this.channelShell = createShellChannel(sshParam, session);
        this.monitor = createMonitor(sshParam);
    }

    private SshMonitor createMonitor(SshParam sshParam) throws SshTangException {
        this.monitor = new SshMonitor(sshParam, this.channelShell);
        monitor.start();
        // 将首次登录连接的命令都清掉
        monitor.cleanLoginEcho();
        return monitor;
    }

    private ChannelShell createShellChannel(SshParam sshParam, ClientSession session) throws SshTangException {
        try {
            ChannelShell channel = session.createShellChannel();
            channel.setPtyType("bash");
            channel.setPtyLines(Integer.MAX_VALUE);
            channel.setPtyColumns(Integer.MAX_VALUE);
            channel.setUsePty(true);
            channel.open().verify(TimeUnit.SECONDS.toMillis(sshParam.getTimeoutSecond()));
            return channel;
        } catch (IOException e) {
            log.error("create shell channel error.", e);
            throw new SshTangException(SshErrorCode.CREATE_CHANNEL_ERROR);
        }
    }

    /**
     * 发送命令
     *
     * @param command 需要发送的命令
     * @return 命令的响应
     * @throws SshTangException 发送失败异常
     */
    public String sendCommand(String command) throws SshTangException {
        return sendCommand(command, true, true, false);
    }

    /**
     * 发送异步命令
     *
     * @param command 需要发送的命令
     * @return 命令的响应
     * @throws SshTangException 发送失败异常
     */
    public String sendSyncCommand(String command) throws SshTangException {
        return sendCommand(command, true, true, true);
    }

    /**
     * 发送指令
     * <p>
     * 使用{@link SshConnection#sendCommand(String)}发送一些持续性命令的时候，比如ping，命令不会主动结束，需要发送一些主动命令，比如Ctrl+C去结束命令
     *
     * @param order 需要发送的指令
     * @throws SshTangException 发送失败异常
     */
    public void sendCommand(SshOrder order) throws SshTangException {
        monitor.sendCommand(order.getCode());
    }

    /**
     * 发送命令, 不打印命令本身
     *
     * @param command 需要发送的命令
     * @return 命令的响应
     * @throws SshTangException 发送失败异常
     */
    public String sendCommandWithoutCommandLog(String command) throws SshTangException {
        return sendCommand(command, false, true, false);
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
    public String sendCommand(String command, boolean logCommand, boolean logEcho,
        boolean isSync) throws SshTangException {
        // TODO: 2023/12/3 实现异步 
        checkConnect();
        if (logCommand) {
            log.info("start send command: {}", command);
        }
        try {
            return send(command, logEcho);
        } catch (SshTangException e) {
            log.error("send command error.", e);
            if (e.getErrorCode() == SshErrorCode.CHANNEL_HAVE_CLOSED) {
                reConnect();
                return send(command, logEcho);
            } else {
                throw e;
            }
        }
    }

    private String send(String command, boolean logEcho) throws SshTangException {
        String result = monitor.sendCommand(command);
        if (logEcho) {
            log.info("receive result: \n{}", result);
        }
        return result;
    }

    private void checkConnect() throws SshTangException {
        if (client == null || session == null) {
            throw new SshTangException(SshErrorCode.CREATE_SESSION_ERROR);
        }
        if (channelShell.isClosed()) {
            log.warn("channel {}@{}:{} has closed.", sshParam.getUsername(), sshParam.getHost(), sshParam.getPort());
            CloseUtils.close(this.monitor, this.channelShell);
            reConnect();
        }
    }

    private void reConnect() throws SshTangException {
        // TODO: 2023/12/3  并发
        log.info("start reconnect {}@{}:{} channel.", sshParam.getUsername(), sshParam.getHost(), sshParam.getPort());
        // 只创建shellChannel行不行？
        this.channelShell = createShellChannel(sshParam, session);
        this.monitor = createMonitor(sshParam);
        log.info("finish reconnect {}@{}:{} channel.", sshParam.getUsername(), sshParam.getHost(), sshParam.getPort());
    }

    @Override
    public void close() throws IOException {
        CloseUtils.close(monitor, session, client);
        monitor = null;
        session = null;
        client = null;
    }
}
/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.base.utils.CloseUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.PtyCapableChannelSession;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.PtyChannelConfigurationHolder;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;

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
 * todo 命令超时机制，top等命令的支持, 并发执行命令(不能同时执行两个非并发命令)
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
public class SshConnection implements Closeable {
    @Getter
    private final SshParam sshParam;

    private SshClient client;

    private ClientSession session;

    private ChannelShell channelShell;

    private SshMonitor monitor;

    private SftpConnection sftpConnection;

    @Getter
    private boolean close = false;

    public SshConnection(SshParam sshParam, SshClient client, ClientSession session) throws SshTangException {
        this.client = client;
        this.session = session;
        this.sshParam = sshParam;
        this.channelShell = createShellChannel(sshParam, session);
        this.monitor = createMonitor(sshParam);
    }

    private SshMonitor createMonitor(SshParam sshParam) {
        this.monitor = new SshMonitor(sshParam, this.channelShell);
        Thread.startVirtualThread(monitor);
        // 将首次登录连接的命令都清掉
        monitor.cleanLoginEcho();
        return monitor;
    }

    /**
     * xterm类型才能支持top命令, 不过直接发送top命令会有乱码, 最好使用top -b -n 1(b,批处理模式，n执行n次)
     * <p>
     * 可以参考{@link PtyCapableChannelSession#resolvePtyType(PtyChannelConfigurationHolder)}
     * 这里设置为xterm(命令行执行echo $TERM获取终端类型)，部分命令的回显会带有乱码，需要先执行命令：bind 'set enable-bracketed-paste off'，
     * 最好设置为bash，否则日志中可能乱码
     *
     * @param sshParam ssh连接参数
     * @param session  连接session
     * @return 创建好的通道
     * @throws SshTangException 创建失败
     * @see <a href="https://askubuntu.com/questions/662222/why-bracketed-paste-mode-is-enabled-sporadically-in-my-terminal-screen">Why bracketed paste mode is enabled sporadically in my terminal screen</a>
     * @see <a href="https://stackoverflow.com/questions/42212099/how-do-i-disable-the-weird-characters-from-bracketed-paste-mode-on-the-mac-os">How do I disable the weird characters from "bracketed paste mode" on the Mac OS X default terminal</a>
     */
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
     * <p>
     * 注意: 发送的异步命令，如果有回显，则会在下一次有效命令时返回
     * <p>
     * 比如,如果执行了ping命令，则只有发送ctrl+c才算有效命令，因为在ping过程中，其余命令不会执行
     *
     * @param command 需要发送的命令
     * @throws SshTangException 发送失败异常
     */
    public void sendCommandAsync(String command) throws SshTangException {
        sendCommand(command, true, true, true);
    }

    /**
     * 发送指令
     * <p>
     * 使用{@link SshConnection#sendCommand(String)}发送一些持续性命令的时候，比如ping，命令不会主动结束，需要发送一些主动命令，比如Ctrl+C去结束命令
     *
     * @param order 需要发送的指令
     * @throws SshTangException 发送失败异常
     */
    public String sendCommand(SshOrder order) throws SshTangException {
        return sendCommand(order, true);
    }

    /**
     * 发送指令
     *
     * @param order   指令
     * @param logEcho 是否打印回显
     * @return 指令执行结果
     * @throws SshTangException 发送失败
     */
    public String sendCommand(SshOrder order, boolean logEcho) throws SshTangException {
        log.info("start send order: {}", order);
        String result = monitor.sendCommand(order.getCode());
        if (logEcho) {
            log.info("receive result: \n{}", result);
        }
        return result;
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
    public String sendCommand(String command, boolean logCommand, boolean logEcho, boolean async)
        throws SshTangException {
        checkConnect();
        if (logCommand) {
            log.info("start send command: {}", command);
        }
        try {
            return send(command, logEcho, async);
        } catch (SshTangException e) {
            log.error("send command error.", e);
            if (e.getErrorCode() == SshErrorCode.CHANNEL_HAVE_CLOSED) {
                reConnect();
                return send(command, logEcho, async);
            } else {
                throw e;
            }
        }
    }

    private String send(String command, boolean logEcho, boolean async) throws SshTangException {
        String result;
        if (async) {
            result = monitor.sendCommand(command, true);
        } else {
            result = monitor.sendCommand(command);
            if (logEcho) {
                log.debug("receive result: \n{}", result);
            }
        }
        return cleanResult(command, result, logEcho);
    }

    private String cleanResult(String command, String result, boolean logEcho) {
        // 清理掉回显的命令本身
        result = result.replace(command, "").trim();
        int lastLineIndex = result.lastIndexOf("\n");
        if (lastLineIndex != -1) {
            // 一般最后一行是shell的开头，属于无效回显
            result = result.substring(0, lastLineIndex).trim();
        } else {
            // 有些命令没有回显
            result = "";
        }
        if (logEcho) {
            log.info("clean result:\n{}", result);
        }
        return result;
    }

    private void checkConnect() throws SshTangException {
        if (isClose()) {
            throw new SshTangException(SshErrorCode.SSH_CONN_HAVE_CLOSE);
        }
        if (channelShell.isClosed()) {
            log.warn("channel {}@{}:{} has closed.", sshParam.getUsername(), sshParam.getHost(), sshParam.getPort());
            CloseUtils.close(this.monitor, this.channelShell);
            reConnect();
        }
    }

    private synchronized void reConnect() throws SshTangException {
        if (!this.channelShell.isClosed()) {
            return;
        }
        log.info("start reconnect {}@{}:{} channel.", sshParam.getUsername(), sshParam.getHost(), sshParam.getPort());
        // 只创建shellChannel行不行？
        this.channelShell = createShellChannel(sshParam, session);
        this.monitor = createMonitor(sshParam);
        log.info("finish reconnect {}@{}:{} channel.", sshParam.getUsername(), sshParam.getHost(), sshParam.getPort());
    }

    /**
     * 创建sftp连接
     *
     * @return sftp连接
     * @throws SshTangException 创建失败
     */
    public SftpConnection createSftpConnection() throws SshTangException {
        checkConnect();
        if (sftpConnection != null && !sftpConnection.isClose()) {
            return sftpConnection;
        }
        log.info("create sftp connection.");
        try {
            SftpClient sftpClient = SftpClientFactory.instance().createSftpClient(session);
            sftpConnection = new SftpConnection(sftpClient);
            log.info("create sftp connection success.");
            return sftpConnection;
        } catch (IOException e) {
            log.error("create sftp error.", e);
            throw new SshTangException(SshErrorCode.CRETE_SFTP_FAIL);
        }
    }

    @Override
    public void close() throws IOException {
        CloseUtils.close(this.sftpConnection, this.monitor, this.session, this.client);
        this.monitor = null;
        this.session = null;
        this.client = null;
        this.close = true;
    }
}
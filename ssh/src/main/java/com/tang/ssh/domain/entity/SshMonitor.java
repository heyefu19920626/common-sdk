/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.utils.CloseUtils;
import com.tang.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 * ssh监视器
 * <p>
 * ssh通道，需要单独的线程去读取输出流
 * <p>
 * todo 超时机制, 并发调用的问题，需要持续输出的命令（top）
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
public class SshMonitor implements Closeable, Runnable {
    private final SshParam sshParam;

    private boolean isOpen = true;

    private boolean hasCleanLoginEcho = false;

    private final ClientChannel channel;

    private final OutputStream out;

    private final InputStream stand;

    private final InputStream error;

    private final StringBuilder cache = new StringBuilder();

    public SshMonitor(SshParam sshParam, ClientChannel channel) {
        this.sshParam = sshParam;
        this.channel = channel;
        out = channel.getInvertedIn();
        stand = channel.getInvertedOut();
        error = channel.getInvertedErr();
    }

    @Override
    public void run() {
        String threadName = String.format("shell-monitor-%s-%s", this.sshParam.getHost(), System.currentTimeMillis());
        Thread.currentThread().setName(threadName);
        log.info("start monitor {} ssh.", sshParam.getHost());
        while (isOpen) {
            if (channel.isEofSignalled() || channel.isClosed()) {
                log.error("{} shell channel close.", sshParam.getHost());
                isOpen = false;
                break;
            }
            readFromChannelInput(stand, "stand");
            readFromChannelInput(error, "err");
            ThreadUtils.sleep(1, TimeUnit.SECONDS);
        }
        log.info("end monitor {} ssh. remain result:\n{}", sshParam.getHost(), getResult());
    }

    public void cleanLoginEcho() {
        long startTime = System.currentTimeMillis();
        while (!hasCleanLoginEcho && isCommandNotOver() && isNotTimeout(startTime)) {
            ThreadUtils.sleep(1, TimeUnit.SECONDS);
        }
        log.info("login info:\n{}\n", getResult());
        hasCleanLoginEcho = true;
    }

    private boolean isNotTimeout(long startTime) {
        return System.currentTimeMillis() - startTime < TimeUnit.SECONDS.toMillis(sshParam.getTimeoutSecond());
    }

    public String sendCommand(String command) throws SshTangException {
        return sendCommand(command, false);
    }

    public String sendCommand(String command, boolean async) throws SshTangException {
        send(command);
        if (async) {
            return "";
        }
        return getEcho();
    }

    private String getEcho() {
        long startTime = System.currentTimeMillis();
        while (isOpen && isCommandNotOver() && isNotTimeout(startTime)) {
            ThreadUtils.sleep(1, TimeUnit.SECONDS);
        }
        return getResult();
    }

    public String sendCommand(int code) throws SshTangException {
        send(code);
        return getEcho();
    }

    private void send(int command) throws SshTangException {
        check();
        try {
            out.write(command);
            out.flush();
            channel.waitFor(EnumSet.of(ClientChannelEvent.OPENED), Duration.ofSeconds(sshParam.getTimeoutSecond()));
        } catch (IOException e) {
            log.error("send {} command error.", sshParam.getHost(), e);
            throw new SshTangException(SshErrorCode.SEND_COMMAND_ERROR);
        }
    }

    private void check() throws SshTangException {
        if (channel.isClosed()) {
            log.warn("channel is closed");
            throw new SshTangException(SshErrorCode.CHANNEL_HAVE_CLOSED);
        }
    }

    private void send(String command) throws SshTangException {
        check();
        try {
            out.write(command.getBytes());
            out.write("\n".getBytes());
            out.flush();
            channel.waitFor(EnumSet.of(ClientChannelEvent.OPENED), Duration.ofSeconds(sshParam.getTimeoutSecond()));
        } catch (IOException e) {
            log.error("send {} command error.", sshParam.getHost(), e);
            if (e.getMessage().contains("closed")) {
                throw new SshTangException(SshErrorCode.CHANNEL_HAVE_CLOSED);
            }
            throw new SshTangException(SshErrorCode.SEND_COMMAND_ERROR);
        }
    }

    private String getResult() {
        String result = cache.toString();
        cache.setLength(0);
        cache.trimToSize();
        return result;
    }

    private boolean isCommandNotOver() {
        // 有任意一个结束符匹配到，就算结束
        return sshParam.getOverSign().stream().noneMatch(overSign -> cache.toString().trim().endsWith(overSign));
    }

    private void readFromChannelInput(InputStream in, String type) {
        try {
            int len;
            byte[] bytes = new byte[2048];
            while ((len = in.available()) > 0) {
                if (len > 2048) {
                    len = 2048;
                }
                len = in.read(bytes, 0, len);
                if (len > 0) {
                    String echo = new String(bytes, 0, len, sshParam.getEncoding());
                    if ("err".equals(type)) {
                        log.warn("receive error:\n{}", echo);
                    } else {
                        cache.append(echo);
                    }
                }
            }
        } catch (IOException e) {
            log.error("read {} input stream error.", sshParam.getHost(), e);
        }
    }

    @Override
    public void close() throws IOException {
        log.info("close {} ssh monitor.", sshParam.getHost());
        isOpen = false;
        CloseUtils.close(out, error, stand, channel);
    }
}
/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ClientChannel;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * ssh监视器
 * <p>
 * ssh通道，需要单独的线程去读取输出流
 * <p>
 * todo 超时机制
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
public class SshMonitor extends Thread implements Closeable {
    private final SshParam sshParam;

    private boolean isOpen = true;

    private boolean isCleanLoginData = false;

    private ClientChannel channel;

    private OutputStream out;

    private InputStream stand;

    private InputStream error;

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
        log.info("start monitor {} ssh.", sshParam.getHost());
        while (isOpen) {
            readFromChannelInput(stand, "stand");
            readFromChannelInput(error, "err");
            ThreadUtils.sleep(1, TimeUnit.SECONDS);
        }
        log.info("end monitor {} ssh.", sshParam.getHost());
    }

    public void cleanLoginEcho() {
        while (!isCleanLoginData && isCommandNotOver()) {
            ThreadUtils.sleep(1, TimeUnit.SECONDS);
        }
        log.info("\n{}\n", getResult());
    }

    public String sendCommand(String command) throws SshTangException {
        // 清空上一次的缓存
        if (!cache.isEmpty()) {
            while (isCommandNotOver()) {
                ThreadUtils.sleep(1, TimeUnit.SECONDS);
            }
            log.info("last cache: \n{}", getResult());
        }
        try {
            out.write(command.getBytes());
            out.write("\n".getBytes());
            out.flush();
        } catch (IOException e) {
            log.error("send {} command error.", sshParam.getHost(), e);
            throw new SshTangException(SshErrorCode.SEND_COMMAND_ERROR);
        }
        while (isCommandNotOver()) {
            ThreadUtils.sleep(1, TimeUnit.SECONDS);
        }
        return getResult();
    }

    private String getResult() {
        String result = cache.toString();
        cache.setLength(0);
        cache.trimToSize();
        return result;
    }

    private boolean isCommandNotOver() {
        // 有任意一个结束符匹配到，就算结束
        return sshParam.getOverSign().stream().noneMatch(overSign -> cache.toString().endsWith(overSign));
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
    }
}
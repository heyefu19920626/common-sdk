/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.manager;

import com.tang.ssh.domain.entity.SshConnection;
import com.tang.ssh.domain.entity.SshOrder;
import com.tang.ssh.domain.entity.SshParam;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.utils.ThreadUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ssh管理器测试
 */
class SshManagerTest {
    @Test
    @DisplayName("参数正确时，获取回显成功")
    void should_get_echo_success_when_param_right() throws SshTangException, IOException {
        SshParam sshParam =
            SshParam.builder().host("192.168.209.129").port(22).username("test").password("123456")
                .build();
        try (SshConnection sshConnection = SshManager.create(sshParam)) {
            String pwd = sshConnection.sendCommand("pwd");
            Assertions.assertTrue(pwd.contains("/home/test"));
        }
    }

    @Test
    @DisplayName("参数正确时，获取回显成功")
    void should_get_echo_success_when_param_right1() throws SshTangException, IOException {
        SshParam sshParam =
            SshParam.builder().host("192.168.209.129").port(22).username("test").password("123456")
                .build();
        try (SshConnection sshConnection = SshManager.create(sshParam)) {
            new Thread(() -> {
                try {
                    sshConnection.sendCommand("ping www.baidu.com");
                } catch (SshTangException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            ThreadUtils.sleep(5, TimeUnit.SECONDS);
            sshConnection.sendCommand(SshOrder.CTRL_C);
            ThreadUtils.sleep(100, TimeUnit.SECONDS);
            sshConnection.sendCommand("date");
        }
    }
}
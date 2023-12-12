/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.application;

import com.tang.ssh.application.SshConnectionManager;
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
class SshConnectionManagerTest {
    @Test
    @DisplayName("参数正确时，获取回显成功")
    void should_get_echo_success_when_param_right() throws SshTangException, IOException {
        SshParam sshParam = getSshParam();
        try (SshConnection sshConnection = SshConnectionManager.create(sshParam)) {
            String pwd = sshConnection.sendCommand("pwd");
            Assertions.assertEquals("/home/test", pwd);
        }
    }

    @Test
    @DisplayName("使用连接池时，获取的连接是同一个")
    void should_return_same_conn_when_use_conn_pool() throws SshTangException {
        SshParam sshParam = getSshParam();
        SshConnection sshConnection = SshConnectionManager.create(sshParam);
        Assertions.assertEquals(sshConnection, SshConnectionManager.create(sshParam));
    }

    @Test
    @DisplayName("释放连接后，再次创建连接成功")
    void should_second_create_success_when_after_release() throws SshTangException, IOException {
        SshParam sshParam = getSshParam();
        SshConnection sshConnection = SshConnectionManager.create(sshParam);
        String pwd = sshConnection.sendCommand("pwd");
        Assertions.assertEquals("/home/test", pwd);
        SshConnectionManager.releaseSshConnection(sshConnection);
        try (SshConnection connection = SshConnectionManager.create(sshParam)) {
            Assertions.assertEquals("/home/test", connection.sendCommand("pwd"));
        }
    }

    @Test
    @DisplayName("异步发送Ctrl+C按键成功")
    void should_send_ctrl_c_success_when_send_ctrl_c() throws SshTangException, IOException {
        SshParam sshParam = getSshParam();
        try (SshConnection sshConnection = SshConnectionManager.create(sshParam)) {
            sshConnection.sendCommandAsync("ping www.baidu.com");
            ThreadUtils.sleep(2, TimeUnit.SECONDS);
            String echo = sshConnection.sendCommand(SshOrder.CTRL_C);
            Assertions.assertTrue(echo.contains("bytes from"));
        }
    }

    @Test
    @DisplayName("发送top命令成功")
    void should_send_top_and_ctrl_c() throws SshTangException, IOException {
        SshParam sshParam = getSshParam();
        try (SshConnection sshConnection = SshConnectionManager.create(sshParam)) {
            sshConnection.sendCommandAsync("top");
            ThreadUtils.sleep(2, TimeUnit.SECONDS);
            String echo = sshConnection.sendCommand(SshOrder.CTRL_C);
            Assertions.assertTrue(echo.contains("%CPU"));
            echo = sshConnection.sendCommand("top -b -n 1");
            Assertions.assertTrue(echo.contains("%CPU"));
        }
    }

    private SshParam getSshParam() {
        return SshParam.builder().host("192.168.209.129").port(22).username("test").password("123456").build();
    }
}
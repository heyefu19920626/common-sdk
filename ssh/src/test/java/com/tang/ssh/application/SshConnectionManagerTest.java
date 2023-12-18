/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.application;

import com.tang.base.exception.BaseException;
import com.tang.base.utils.ThreadUtils;
import com.tang.ssh.domain.entity.SshOrder;
import com.tang.ssh.domain.entity.SshParam;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.ssh.domain.service.SshConnection;
import com.tang.ssh.domain.utils.SshTestUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ssh管理器测试
 */
class SshConnectionManagerTest {
    private static SshServer sshd;

    private static SshServer sshJump;

    @BeforeAll
    static void beforeAll() throws IOException {
        sshd = SshTestUtils.createSshServer(0);
        sshJump = SshTestUtils.createSshServer(1);
        sshJump.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
    }

    @Test
    @DisplayName("当使用ssh跳转的时候连接成功")
    void should_connect_success_when_use_ssh_jump() throws BaseException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, sshJump);
        try (SshConnection connection = SshConnectionManager.create(sshParam)) {
            String pwd = connection.sendCommand("pwd");
            Assertions.assertEquals("/home/test", pwd);
        }
    }

    @Test
    @DisplayName("参数正确时，获取回显成功")
    void should_get_echo_success_when_param_right() throws SshTangException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
        try (SshConnection sshConnection = SshConnectionManager.create(sshParam)) {
            String pwd = sshConnection.sendCommand("pwd");
            Assertions.assertEquals("/home/test", pwd);
        }
    }

    @Test
    @DisplayName("使用连接池时，获取的连接是同一个")
    void should_return_same_conn_when_use_conn_pool() throws SshTangException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
        SshConnection sshConnection = SshConnectionManager.create(sshParam);
        Assertions.assertEquals(sshConnection, SshConnectionManager.create(sshParam));
    }

    @Test
    @DisplayName("释放连接后，再次创建连接成功")
    void should_second_create_success_when_after_release() throws SshTangException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
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
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
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
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
        try (SshConnection sshConnection = SshConnectionManager.create(sshParam)) {
            String echo = sshConnection.sendCommand("top -b -n 1");
            Assertions.assertTrue(echo.contains("%CPU"));
        }
    }

    @Test
    @DisplayName("当连接已关闭后执行命令时抛出异常")
    void should_throw_exception_when_conn_close() throws SshTangException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
        SshConnection sshConnection = SshConnectionManager.create(sshParam);
        sshConnection.close();
        SshTangException exception = Assertions.assertThrows(SshTangException.class,
            () -> sshConnection.sendCommand("pwd"));
        Assertions.assertEquals(SshErrorCode.SSH_CONN_HAVE_CLOSE, exception.getErrorCode());
    }

    @AfterAll
    static void afterAll() throws IOException {
        sshd.close();
        sshJump.close();
    }
}
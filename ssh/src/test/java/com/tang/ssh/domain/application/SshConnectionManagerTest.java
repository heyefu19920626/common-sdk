/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.application;

import com.tang.ssh.application.SshConnectionManager;
import com.tang.ssh.domain.entity.SshConnection;
import com.tang.ssh.domain.entity.SshOrder;
import com.tang.ssh.domain.entity.SshParam;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.ssh.domain.utils.CommandExecutionHelper;
import com.tang.ssh.domain.utils.SshTestUtils;
import com.tang.utils.ThreadUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.channel.ChannelSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * ssh管理器测试
 */
class SshConnectionManagerTest {
    private static SshServer sshd;
    private static int port;

    @BeforeAll
    static void beforeAll() throws IOException {
        sshd = SshTestUtils.setupTestServer();
        port = sshd.getPort();
        sshd.setShellFactory((session) -> new CommandExecutionHelper() {
            @Override
            protected boolean handleCommandLine(String command) throws Exception {
                OutputStream stdout = getOutputStream();
                String resp;
                if ("pwd".equals(command)) {
                    resp = "%s\n%s\n$".formatted(command, "/home/test");
                } else if (command.startsWith("top")) {
                    resp = "%s\n%s\n$".formatted(command, "%CPU");
                } else if (command.startsWith("ping")) {
                    resp = "%s\n%s\n$".formatted(command, "bytes from");
                } else {
                    resp = "%s\n$".formatted(command);
                }
                stdout.write(resp.getBytes(StandardCharsets.UTF_8));
                stdout.flush();
                return true;
            }

            @Override
            public void start(ChannelSession channel, Environment env) throws IOException {
                super.start(channel, env);
                OutputStream stdout = getOutputStream();
                stdout.write("$".getBytes(StandardCharsets.UTF_8));
                stdout.flush();
            }
        });
    }


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
            String echo = sshConnection.sendCommand("top -b -n 1");
            Assertions.assertTrue(echo.contains("%CPU"));
        }
    }

    private SshParam getSshParam() {
        // return SshParam.builder().host("192.168.209.129").port(22).username("test").password("123456").build();
        return SshParam.builder().host("127.0.0.1").port(port).username("test").password("123456").build();
    }

    @AfterAll
    static void afterAll() throws IOException {
        sshd.close();
    }
}
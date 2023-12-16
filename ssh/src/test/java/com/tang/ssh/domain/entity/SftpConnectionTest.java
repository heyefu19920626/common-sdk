/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import com.tang.base.exception.BaseException;
import com.tang.ssh.application.SshConnectionManager;
import com.tang.ssh.domain.exception.SshTangException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;

/**
 * sftp测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SftpConnectionTest {
    @Order(0)
    @Test
    @DisplayName("当使用ssh时上传成功")
    void should_upload_success_when_use_ssh() throws BaseException, IOException {
        SshParam sshParam = getSshParamByJump(false);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection();
        ) {
            String ipAddr = connection.sendCommand("ip addr");
            Assertions.assertTrue(ipAddr.contains(sshParam.getHost()));
            sftpConnection.upload("pom.xml", "/home/test/pom.xml");
            String ls = connection.sendCommand("ls");
            Assertions.assertTrue(ls.contains("pom.xml"));
        }
    }

    @Test
    @Order(1)
    @DisplayName("当使用ssh时下载成功")
    void should_download_success_when_use_ssh() throws SshTangException, IOException {
        SshParam sshParam = getSshParamByJump(false);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection();
        ) {
            String ipAddr = connection.sendCommand("ip addr");
            Assertions.assertTrue(ipAddr.contains(sshParam.getHost()));
            sftpConnection.download("/home/test/pom.xml", "target/pom.xml");
            connection.sendCommand("rm -f /home/test/pom.xml");
        }
    }

    @Order(0)
    @Test
    @DisplayName("当使用ssh跳转时上传成功")
    void should_upload_success_when_use_ssh_jump() throws BaseException, IOException {
        SshParam sshParam = getSshParamByJump(true);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection();
        ) {
            String ipAddr = connection.sendCommand("ip addr");
            Assertions.assertTrue(ipAddr.contains(sshParam.getHost()));
            sftpConnection.upload("pom.xml", "/home/test/pom.xml");
            String ls = connection.sendCommand("ls");
            Assertions.assertTrue(ls.contains("pom.xml"));
        }
    }

    @Test
    @Order(1)
    @DisplayName("当使用ssh跳转时下载成功")
    void should_download_success_when_use_ssh_jump() throws SshTangException, IOException {
        SshParam sshParam = getSshParamByJump(true);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection();
        ) {
            String ipAddr = connection.sendCommand("ip addr");
            Assertions.assertTrue(ipAddr.contains(sshParam.getHost()));
            sftpConnection.download("/home/test/pom.xml", "target/pom.xml");
            connection.sendCommand("rm -f /home/test/pom.xml");
        }
    }

    SshParam getSshParamByJump(boolean hasJump) {
        SshJumpParam jumpParam = null;
        if (hasJump) {
            jumpParam = SshJumpParam.builder().host("192.168.209.129").username("test").password("123456").build();
            return SshParam.builder().sshJumpParam(jumpParam).host("192.168.209.133").username("test").password("123456")
                .build();
        }
        return SshParam.builder().sshJumpParam(jumpParam).host("192.168.209.129").username("test").password("123456")
            .build();
    }
}
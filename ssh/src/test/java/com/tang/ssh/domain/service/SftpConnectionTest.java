/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.service;

import com.tang.base.exception.BaseException;
import com.tang.ssh.application.SshConnectionManager;
import com.tang.ssh.domain.entity.SshParam;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.ssh.domain.utils.SshTestUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;

/**
 * sftp测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SftpConnectionTest {
    private static SshServer sshd;

    private static SshServer sshdJump;

    @BeforeAll
    static void beforeAll() throws IOException {
        sshd = SshTestUtils.createWithSftpServer(0);
        sshdJump = SshTestUtils.createSshServer(1);
        sshdJump.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
    }

    @Order(0)
    @Test
    @DisplayName("当使用ssh时上传成功")
    void should_upload_success_when_use_ssh() throws BaseException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection()
        ) {
            sftpConnection.upload("pom.xml", "pom.xml");
            Assertions.assertTrue(new File("target/pom.xml").delete());
        }
    }

    @Test
    @Order(1)
    @DisplayName("当使用ssh时下载成功")
    void should_download_success_when_use_ssh() throws SshTangException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, null);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection()
        ) {
            sftpConnection.download("hostkey_0.EC", "target/hostkey_test.EC");
            Assertions.assertTrue(new File("target/hostkey_test.EC").delete());
        }
    }

    @Order(0)
    @Test
    @DisplayName("当使用ssh跳转时上传成功")
    void should_upload_success_when_use_ssh_jump() throws BaseException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, sshdJump);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection();
        ) {
            sftpConnection.upload("pom.xml", "pom.xml");
            Assertions.assertTrue(new File("target/pom.xml").delete());
        }
    }

    @Test
    @Order(1)
    @DisplayName("当使用ssh跳转时下载成功")
    void should_download_success_when_use_ssh_jump() throws SshTangException, IOException {
        SshParam sshParam = SshTestUtils.createSshParam(sshd, sshdJump);
        try (SshConnection connection = SshConnectionManager.create(sshParam);
            SftpConnection sftpConnection = connection.createSftpConnection();
        ) {
            sftpConnection.download("hostkey_0.EC", "target/hostkey_test.EC");
            Assertions.assertTrue(new File("target/hostkey_test.EC").delete());
        }
    }

    @AfterAll
    static void afterAll() throws IOException {
        sshd.close();
        sshdJump.close();
    }
}
/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.utils;

import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.UnknownCommandFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

/**
 * ssh测试工具
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/15
 */
public class SshTestUtils {
    /**
     * 启动一个本地ssh服务器, 需要自己设置shell工厂
     *
     * @return ssh服务
     * @throws IOException 启动失败
     */
    public static SshServer setupTestServer() throws IOException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setKeyPairProvider(
            createTestHostKeyProvider(new File("target/hostkey." + KeyUtils.EC_ALGORITHM).toPath()));
        sshd.setPasswordAuthenticator(
            (username, password, session) -> "test".equals(username) && "123456".equals(password));
        sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        // sshd.setShellFactory(EchoShellFactory.INSTANCE);
        sshd.setCommandFactory(UnknownCommandFactory.INSTANCE);
        CoreModuleProperties.NIO2_READ_TIMEOUT.set(sshd, Duration.ofSeconds(60));
        sshd.start();
        return sshd;
    }

    private static KeyPairProvider createTestHostKeyProvider(Path path) {
        SimpleGeneratorHostKeyProvider keyProvider = new SimpleGeneratorHostKeyProvider();
        keyProvider.setPath(Objects.requireNonNull(path, "No path"));
        keyProvider.setAlgorithm(KeyUtils.EC_ALGORITHM);
        keyProvider.setKeySize(256);
        return keyProvider;
    }
}
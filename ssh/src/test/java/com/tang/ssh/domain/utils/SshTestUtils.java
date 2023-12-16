/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.utils;

import com.tang.ssh.domain.entity.SshJumpParam;
import com.tang.ssh.domain.entity.SshParam;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.UnknownCommandFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
    private static final String host = "127.0.0.1";

    private static final String username = "test";

    private static final String password = "123456";

    /**
     * 创建测试的ssh连接参数
     *
     * @param sshd     需要连接的ssh服务器
     * @param sshdJump 用来跳转的ssh服务器
     * @return ssh连接参数
     */
    public static SshParam createSshParam(SshServer sshd, SshServer sshdJump) {
        if (sshdJump != null) {
            // SshJumpParam jumpParam = SshJumpParam.builder().host("192.168.209.129").username("test").password("123456") .build();
            SshJumpParam jumpParam =
                SshJumpParam.builder().host(host).port(sshdJump.getPort()).username(username).password(password)
                    .build();
            // return SshParam.builder().sshJumpParam(jumpParam).host("192.168.209.133").username("test")
            return SshParam.builder().sshJumpParam(jumpParam).host(host).port(sshd.getPort()).username(username)
                .password(password).build();
        }
        // return SshParam.builder().host("192.168.209.129").username("test").password("123456") .build();
        return SshParam.builder().host(host).port(sshd.getPort()).username(username).password(password).build();
    }

    /**
     * 启动一个本地ssh服务器, 需要自己设置shell工厂
     *
     * @param i 创建的第几个服务器，用来创建主机秘钥
     * @return ssh服务
     * @throws IOException 启动失败
     */
    public static SshServer setupTestServer(int i) throws IOException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setKeyPairProvider(
            createTestHostKeyProvider(new File("target/hostkey_%s.%s".formatted(i, KeyUtils.EC_ALGORITHM)).toPath()));
        sshd.setPasswordAuthenticator((user, passwd, session) -> username.equals(user) && password.equals(passwd));
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

    /**
     * 创建shell工厂
     *
     * @return shell工厂
     */
    public static CommandExecutionHelper createShellFactory() {
        return new CommandExecutionHelper() {
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
        };
    }
}
/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.application;

import com.tang.base.utils.CloseUtils;
import com.tang.ssh.domain.entity.BasicAuthParam;
import com.tang.ssh.domain.entity.SshParam;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.ssh.domain.service.SshConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.UserAuthFactory;
import org.apache.sshd.client.auth.keyboard.UserAuthKeyboardInteractiveFactory;
import org.apache.sshd.client.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.core.CoreModuleProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ssh管理器
 * <p>
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
public class SshConnectionManager {
    private static final Map<String, SshConnection> CONN_POOL = new ConcurrentHashMap<>();

    /**
     * 根据连接参数创建ssh连接, 首先从连接池获取, 释放连接也请优先使用{@link SshConnectionManager#releaseSshConnection(SshConnection)}释放
     *
     * @param sshParam 连接参数
     * @return ssh连接
     * @throws SshTangException 创建失败
     */
    public static SshConnection create(SshParam sshParam) throws SshTangException {
        sshParam.check();
        String connName = getConnName(sshParam);
        if (CONN_POOL.containsKey(connName)) {
            SshConnection sshConnection = CONN_POOL.get(connName);
            // 如果被单独调用了连接的关闭方法，则重新创建
            if (sshConnection.isClose()) {
                CONN_POOL.remove(connName);
                return create(sshParam, connName);
            }
            return sshConnection;
        }
        return create(sshParam, connName);
    }

    /**
     * 释放连接, 如果连接没有关闭，则先关闭连接
     *
     * @param sshConnection ssh连接
     */
    public static void releaseSshConnection(SshConnection sshConnection) {
        if (sshConnection != null) {
            SshConnection connection = CONN_POOL.remove(getConnName(sshConnection.getSshParam()));
            if (!connection.isClose()) {
                CloseUtils.close(sshConnection);
            }
        }
    }

    private static String getConnName(SshParam sshParam) {
        return String.format("%s@%s:%s", sshParam.getUsername(), sshParam.getHost(), sshParam.getPort());
    }

    private synchronized static SshConnection create(SshParam sshParam, String connName) throws SshTangException {
        if (CONN_POOL.containsKey(connName)) {
            return CONN_POOL.get(connName);
        }
        log.info("start create ssh({}) session.", connName);
        SshClient client = createClient();
        ClientSession session;
        try {
            BasicAuthParam sshJumpParam = sshParam.getSshJumpParam();
            if (sshParam.getSshJumpParam() != null) {
                return createByJumper(sshParam, connName, client, sshJumpParam);
            }
            session = createSession(sshParam, client);
            SshConnection sshConnection = new SshConnection(sshParam, client, session);
            CONN_POOL.put(connName, sshConnection);
            log.info("finish create ssh({}) session.", connName);
            return sshConnection;
        } catch (IOException e) {
            log.error("create ssh({}) session error.", connName, e);
            handleException(e.getMessage());
            throw new SshTangException(SshErrorCode.CREATE_SESSION_ERROR);
        }
    }

    private static SshConnection createByJumper(SshParam sshParam, String connName, SshClient client,
        BasicAuthParam sshJumpParam) throws IOException, SshTangException {
        client.addPasswordIdentity(sshJumpParam.getPassword());
        String proxyJump = "%s@%s:%s".formatted(sshJumpParam.getUsername(), sshJumpParam.getHost(),
            sshJumpParam.getPort());
        log.info("create conn to {} by ssh jumper: {}", sshParam.getHost(), proxyJump);
        ClientSession sessionByJumper =
            client.connect(new HostConfigEntry("", sshParam.getHost(), sshParam.getPort(),
                    sshParam.getUsername(), proxyJump))
                .verify().getSession();
        sessionByJumper.addPasswordIdentity(sshParam.getPassword());
        sessionByJumper.auth().verify();
        SshConnection sshConnection = new SshConnection(sshParam, client, sessionByJumper);
        CONN_POOL.put(connName, sshConnection);
        log.info("finish create ssh({}) session.", connName);
        return sshConnection;
    }

    private static SshClient createClient() {
        SshClient client = SshClient.setUpDefaultClient();
        CoreModuleProperties.NIO_WORKERS.set(client, 1);
        client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        // 密码方式登录时，只使用密码认证，密码认证包含全密码、键盘交互式两种方式
        List<UserAuthFactory> userAuthFactories = new ArrayList<>();
        userAuthFactories.add(UserAuthPasswordFactory.INSTANCE);
        userAuthFactories.add(UserAuthKeyboardInteractiveFactory.INSTANCE);
        client.setUserAuthFactories(userAuthFactories);
        client.start();
        return client;
    }

    private static void handleException(String message) throws SshTangException {
        if (message.contains("algorithms")) {
            throw new SshTangException(SshErrorCode.SERVER_ALGORITHMS_UN_SUPPORT);
        }
        if (message.contains("authentication")) {
            throw new SshTangException(SshErrorCode.SERVER_AUTHENTICATION_FAIL);
        }
    }

    private static ClientSession createSession(SshParam sshParam, SshClient client) throws IOException {
        ClientSession session;
        session = client.connect(sshParam.getUsername(), sshParam.getHost(), sshParam.getPort())
            .verify(sshParam.getTimeoutSecond(), TimeUnit.SECONDS)
            .getSession();
        session.addPasswordIdentity(sshParam.getPassword());
        session.auth().verify(sshParam.getTimeoutSecond(), TimeUnit.SECONDS);
        return session;
    }
}
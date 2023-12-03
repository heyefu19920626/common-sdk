/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.manager;

import com.tang.ssh.domain.entity.SshConnection;
import com.tang.ssh.domain.entity.SshParam;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.utils.CloseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;
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
            if (!sshConnection.isClose()) {
                CloseUtils.close(sshConnection);
            }
            CONN_POOL.remove(getConnName(sshConnection.getSshParam()));
        }
    }

    private static String getConnName(SshParam sshParam) {
        return STR. "\{ sshParam.getUsername() }@\{ sshParam.getHost() }:\{ sshParam.getPort() }" ;
    }

    private synchronized static SshConnection create(SshParam sshParam, String connName) throws SshTangException {
        if (CONN_POOL.containsKey(connName)) {
            return CONN_POOL.get(connName);
        }
        log.info("start create ssh({}) session.", connName);
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        ClientSession session;
        try {
            session = createSession(sshParam, client);
            SshConnection sshConnection = new SshConnection(sshParam, client, session);
            CONN_POOL.put(connName, sshConnection);
            log.info("finish create ssh({}) session.", connName);
            return sshConnection;
        } catch (IOException e) {
            log.error("create ssh({}) session error.", connName, e);
            throw new SshTangException(SshErrorCode.CREATE_SESSION_ERROR);
        }
    }

    private static ClientSession createSession(SshParam sshParam, SshClient client) throws IOException {
        ClientSession session;
        session = client.connect(sshParam.getUsername(), sshParam.getHost(), sshParam.getPort())
            .verify(sshParam.getTimeoutSecond(), TimeUnit.SECONDS).getSession();
        session.addPasswordIdentity(sshParam.getPassword());
        session.auth().verify(sshParam.getTimeoutSecond(), TimeUnit.SECONDS);
        return session;
    }
}
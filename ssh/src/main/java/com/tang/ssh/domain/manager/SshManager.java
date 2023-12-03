/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.manager;

import com.tang.ssh.domain.entity.SshConnection;
import com.tang.ssh.domain.entity.SshParam;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ssh管理器
 * <p>
 * todo 连接池
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
public class SshManager {
    /**
     * 根据连接参数创建ssh连接
     *
     * @param sshParam 连接参数
     * @return ssh连接
     * @throws SshTangException 创建失败
     */
    public static SshConnection create(SshParam sshParam) throws SshTangException {
        sshParam.check();
        log.info("start create ssh({}@{}:{}) session error.", sshParam.getUsername(), sshParam.getHost(),
            sshParam.getPort());
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        ClientSession session;
        try {
            session = createSession(sshParam, client);
            SshConnection sshConnection = new SshConnection(sshParam, client, session);
            log.info("finish create ssh({}@{}:{}) session.", sshParam.getUsername(), sshParam.getHost(),
                sshParam.getPort());
            return sshConnection;
        } catch (IOException e) {
            log.error("create ssh({}@{}:{}) session error.", sshParam.getUsername(), sshParam.getHost(),
                sshParam.getPort(), e);
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
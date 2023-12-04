/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;

import cn.hutool.core.net.NetUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ssh连接参数
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Getter
@Builder
public class SshParam {
    private String host;

    private int port;

    private String username;

    private String password;

    /**
     * 连接超时秒数
     */
    @Builder.Default
    private int timeoutSecond = 5;

    /**
     * 命令输出结束符
     */
    @Builder.Default
    private Set<String> overSign = new HashSet<>(List.of("$", "Password:", "[Y/n]"));

    /**
     * 命令行输出编码格式
     */
    @Builder.Default
    private String encoding = "utf-8";

    /**
     * 检查参数是否合法
     */
    public void check() throws SshTangException {
        if (!NetUtil.ping(this.host)) {
            throw new SshTangException(SshErrorCode.HOST_CONNECT_FAIL);
        }
    }
}
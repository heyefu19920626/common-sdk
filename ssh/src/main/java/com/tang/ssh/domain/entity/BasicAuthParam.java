/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import cn.hutool.core.net.NetUtil;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基础认证参数
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/15
 */
@Getter
@SuperBuilder
public class BasicAuthParam {
    private String host;

    @Builder.Default
    private Integer port = 22;

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
    private Set<String> overSign = new HashSet<>(List.of("$", "Password:", "[Y/n]", "#"));

    /**
     * 命令行输出编码格式
     */
    @Builder.Default
    private String encoding = "utf-8";

    /**
     * 校验参数是否合法
     *
     * @throws SshTangException 校验失败
     */
    public void check() throws SshTangException {
        if (!NetUtil.ping(this.host)) {
            throw new SshTangException(SshErrorCode.HOST_CONNECT_FAIL.getCode(),
                SshErrorCode.HOST_CONNECT_FAIL.getDescKey(), new String[]{host});
        }
    }
}
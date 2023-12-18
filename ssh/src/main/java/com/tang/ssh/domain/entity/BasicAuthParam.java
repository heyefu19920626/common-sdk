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
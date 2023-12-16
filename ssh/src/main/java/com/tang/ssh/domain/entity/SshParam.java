/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import cn.hutool.core.net.NetUtil;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * ssh连接参数
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Getter
@SuperBuilder
public class SshParam extends BasicAuthParam {
    /**
     * ssh跳转参数, 部分目标主机无法直接连接，需要通过ssh跳板机进行连接
     */
    private BasicAuthParam sshJumpParam;

    /**
     * 检查参数是否合法
     */
    public void check() throws SshTangException {
        if (sshJumpParam == null) {
            if (!NetUtil.ping(getHost())) {
                throw new SshTangException(SshErrorCode.HOST_CONNECT_FAIL);
            }
        } else {
            sshJumpParam.check();
        }
    }
}
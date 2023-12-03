/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ssh指令,主要是一些快捷键
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/3
 */
@Getter
@AllArgsConstructor
public enum SshOrder {
    CTRL_C(3);

    private final int code;
}
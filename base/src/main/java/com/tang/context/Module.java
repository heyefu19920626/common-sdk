/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模块枚举
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Getter
@AllArgsConstructor
public enum Module {
    BASE(1000);

    private final int module;
}
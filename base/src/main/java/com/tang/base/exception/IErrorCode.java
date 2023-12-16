/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.exception;

import com.tang.base.context.Module;

/**
 * 错误码接口
 * <p>
 * 错误码包含： 错误码、错误描述的国际化key、错误描述的参数、该错误建议的国际化key(该key约定为错误描述的key加.suggestion)、建议的参数
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
public interface IErrorCode {
    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误描述的国际化key
     *
     * @return 错误描述的国际化key
     */
    String getDescKey();

    /**
     * 获取错误描述的国际化参数
     *
     * @return 错误描述的国际化参数
     */
    String[] getDescParams();

    /**
     * 获取错误建议的国际化参数
     *
     * @return 错误建议的国际化参数
     */
    String[] getSuggestionParams();

    /**
     * 初始化错误码
     *
     * @param module 错误所属模块
     * @param code   错误在模块内的错误码
     * @return 完整的错误码
     */
    static int initCode(Module module, String code) {
        return Integer.parseInt(module.getModule() + code);
    }
}
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
     * 获取错误所在的模块
     *
     * @return 错误所在的模块
     */
    Module getModule();

    /**
     * 所在模块的错误码
     *
     * @return 模块所在的错误码
     */
    String getModuleErrorCode();

    /**
     * 获取完整的错误码, 由模块码+模块所在错误码构成
     *
     * @return 错误码
     */
    default int getCode() {
        return Integer.parseInt(getModule().getCode() + getModuleErrorCode());
    }

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
    default String[] getDescParams() {
        return new String[]{};
    }

    /**
     * 获取错误建议的国际化参数
     *
     * @return 错误建议的国际化参数
     */
    default String[] getSuggestionParams() {
        return new String[]{};
    }
}
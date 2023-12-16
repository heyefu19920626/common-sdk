/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.response;

import com.tang.base.i18n.I18nUtils;
import com.tang.base.exception.BaseException;
import com.tang.base.exception.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 给前端的响应
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Getter
@AllArgsConstructor
public class Response<T> {
    private int code;
    private T data;
    private String desc;
    private String suggestion;

    /**
     * 获取成功的响应
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(0, data, "", "");
    }

    /**
     * 返回失败响应
     *
     * @param exception 异常
     * @return 失败响应
     */
    public static <T> Response<T> fail(BaseException exception) {
        return fail(exception.getErrorCode());
    }

    /**
     * 返回失败响应
     *
     * @param errorCode 错误码
     * @return 失败响应
     */
    public static <T> Response<T> fail(IErrorCode errorCode) {
        return new Response<>(errorCode.getCode(), null,
            I18nUtils.getMessage(errorCode.getDescKey(), errorCode.getDescParams()),
            I18nUtils.getMessage(errorCode.getDescKey() + ".suggestion", errorCode.getSuggestionParams()));
    }
}
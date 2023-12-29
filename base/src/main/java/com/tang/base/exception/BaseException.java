/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.exception;

import com.tang.base.context.Module;
import com.tang.base.i18n.I18nUtils;
import lombok.Getter;

import java.util.Locale;

/**
 * 基础异常，所有异常都必须由此派生
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Getter
public class BaseException extends Exception {
    private final IErrorCode errorCode;

    public BaseException(IErrorCode errorCode) {
        super(errorCode.getDescKey());
        this.errorCode = errorCode;
    }

    public BaseException(IErrorCode errorCode, String... descParams) {
        super(errorCode.getDescKey());
        this.errorCode = initErrorCode(errorCode, descParams);
    }

    public BaseException(IErrorCode errorCode, String[] descParams, String... suggestionParams) {
        super(errorCode.getDescKey());
        this.errorCode = initErrorCode(errorCode, descParams, suggestionParams);
    }

    private IErrorCode initErrorCode(IErrorCode errorCode, String[] descParams, String... suggestionParams) {
        return new IErrorCode() {
            @Override
            public Module getModule() {
                return errorCode.getModule();
            }

            @Override
            public String getModuleErrorCode() {
                return errorCode.getModuleErrorCode();
            }

            @Override
            public String getDescKey() {
                return errorCode.getDescKey();
            }

            @Override
            public String[] getDescParams() {
                return descParams;
            }

            @Override
            public String[] getSuggestionParams() {
                return suggestionParams;
            }
        };
    }

    @Override
    public String toString() {
        return I18nUtils.getMessage(Locale.ENGLISH, errorCode.getDescKey(), errorCode.getDescParams());
    }
}
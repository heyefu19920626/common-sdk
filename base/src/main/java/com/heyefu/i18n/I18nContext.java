/*
 * Copyright (c) Heyefu Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.heyefu.i18n;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

/**
 * 国际化上下文
 *
 * @author heyefu
 * @version [2023/12/1]
 * @since 2023/12/1
 */
@Slf4j
public class I18nContext {
    private static final ThreadLocal<Locale> LANGUAGE = new ThreadLocal<>();


    /**
     * 获取系统当前线程语言，如果没有设置，返回系统默认语言
     *
     * @return 当前语言
     */
    public static Locale getLanguage() {
        Locale language = LANGUAGE.get();
        if (language == null) {
            // todo 可以先读取配置文件
            language = Locale.getDefault();
            log.warn("not set thread language, use system default {}.", language);
            return language;
        }
        return language;
    }

    /**
     * 设置当前线程国际化语言
     *
     * @param language 当前语言
     */
    public static void setLanguage(Locale language) {
        LANGUAGE.set(language);
    }

    /**
     * 清楚当前线程语言
     */
    public static void clean() {
        LANGUAGE.remove();
    }
}
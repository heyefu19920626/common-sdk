/*
 * Copyright (c) Heyefu Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.heyefu.i18n;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 国际化工具类
 *
 * @author heyefu
 * @version [2023/12/1]
 * @since 2023/12/1
 */
@Slf4j
public class I18nUtils {
    private final static Map<String, ReloadableResourceBundleMessageSource> resources = new HashMap<>();

    /**
     * 获取国际化消息
     *
     * @param code 国际化code
     * @param args 国际化需要的参数
     * @return 翻译后的消息，未找到模块或者未找code，返回code
     */
    public static String getMessage(String code, String... args) {
        if (StringUtils.isBlank(code)) {
            log.warn("i18n code is blank, return empty");
            return "";
        }
        String module = code.split("\\.", 2)[0];
        if (resources.containsKey(module)) {
            return getMessage(resources.get(module), code, args);
        }
        synchronized (I18nUtils.class) {
            if (resources.containsKey(module)) {
                return getMessage(resources.get(module), code, args);
            }
            initResource(module);
        }
        return getMessage(resources.get(module), code, args);
    }

    private static void initResource(String module) {
        log.info("start init i18n resource module: {}", module);
        ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
        resource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        resource.setBasename("i18n/message_" + module);
        resource.setUseCodeAsDefaultMessage(true);
        resources.put(module, resource);
        log.info("finish init i18n resource module: {}", module);
    }

    private static String getMessage(ReloadableResourceBundleMessageSource resource, String code, String... args) {
        return resource.getMessage(code, args, I18nContext.getLanguage());
    }
}
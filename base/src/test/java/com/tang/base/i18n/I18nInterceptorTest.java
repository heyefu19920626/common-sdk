/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.i18n;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Locale;

/**
 * 国际化拦截器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class I18nInterceptorTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("请求语言为中文，返回中文")
    void should_return_zh_when_language_zh() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test").header("Accept-Language", "zh_CN"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(
                "{\"code\":10000001,\"data\":null,\"desc\":\"系统内部错误。\",\"suggestion\":\"请联系技术工程师处理。\"}"));
    }

    @Test
    @DisplayName("请求语言为英文，返回英文")
    void should_return_en_when_language_en() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test").header("Accept-Language", "en"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.desc").value("Internal System Error."));
    }

    @Test
    @DisplayName("请求语言不存在，返回英文")
    void should_return_en_when_language_not_exits() throws Exception {
        System.out.println(Locale.JAPAN);
        mockMvc.perform(MockMvcRequestBuilders.get("/test").header("Accept-Language", "ja_JP"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.desc").value("Internal System Error."));
    }
}
package com.tang.i18n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Locale;

/**
 * 国际化测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class I18nUtilsTest {
    @Test
    @Order(0)
    @DisplayName("当没有设置语言时，应该返回系统默认语言")
    void should_return_default_when_not_set_language() {
        String message = I18nUtils.getMessage("base.system.internal.error");
        if (Locale.getDefault().equals(Locale.ENGLISH)) {
            Assertions.assertEquals("Internal System Error.", message);
        } else {
            Assertions.assertEquals("系统内部错误。", message);
        }
    }

    @Test
    @DisplayName("当设置语言为英文时，应该返回英文")
    void should_return_en_when_set_en() {
        I18nContext.setLanguage(Locale.ENGLISH);
        String message = I18nUtils.getMessage("base.system.internal.error");
        Assertions.assertEquals("Internal System Error.", message);
        I18nContext.clean();
    }

    @Test
    @DisplayName("当code不存在时，返回code")
    void should_return_code_when_code_not_exits() {
        I18nContext.setLanguage(Locale.CHINA);
        String message = I18nUtils.getMessage("base.system.internal.error.not.exits");
        Assertions.assertEquals("base.system.internal.error.not.exits", message);
        I18nContext.clean();
    }

    @Test
    @DisplayName("当模块不存在时，返回code")
    void should_return_code_when_module_not_exits() {
        I18nContext.setLanguage(Locale.CHINA);
        String message = I18nUtils.getMessage("not.exits.base.system.internal.error");
        Assertions.assertEquals("not.exits.base.system.internal.error", message);
        I18nContext.clean();
    }

    @Test
    @DisplayName("当code为空时，返回空字符串")
    void should_return_empty_when_code_empty() {
        I18nContext.setLanguage(Locale.CHINA);
        String message = I18nUtils.getMessage("");
        Assertions.assertEquals("", message);
        I18nContext.clean();
    }
}
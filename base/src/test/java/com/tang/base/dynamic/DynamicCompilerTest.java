/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.dynamic;

import com.tang.base.exception.BaseErrorCode;
import com.tang.base.exception.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * 动态编译测试
 */
class DynamicCompilerTest {
    @Test
    @DisplayName("当文件后缀名非法时，抛出文件后缀名非法错误")
    void should_throw_file_suffix_illegal_when_file_suffix_illegal() {
        String path = "base/target/classes/com/tang/context/Module.class";
        BaseException exception = Assertions.assertThrows(BaseException.class,
            () -> DynamicCompiler.compilerToClass(path));
        Assertions.assertEquals(BaseErrorCode.FILE_SUFFIX_ILLEGAL, exception.getErrorCode());
    }

    @Test
    @DisplayName("当文件格式非法时，抛出文件格式非法错误")
    void should_throw_file_format_illegal_when_file_format_illegal() {
        String path = "base/target/classes/com/tang/context/Module.java";
        BaseException exception = Assertions.assertThrows(BaseException.class,
            () -> DynamicCompiler.compilerToClass(path));
        Assertions.assertEquals(BaseErrorCode.FILE_FORMAT_ILLEGAL, exception.getErrorCode());
    }

    @Test
    @DisplayName("当编译成功，返回clas文件路径")
    void should_return_class_path_when_compiler_success() {
        String path = "src/main/java/com/tang/base/i18n/I18nUtils.java";
        String classPath = Assertions.assertDoesNotThrow(() -> DynamicCompiler.compilerToClass(path));
        Assertions.assertTrue(classPath.endsWith(".class"));
    }

    @Test
    @DisplayName("当编译失败，抛出编译失败错误,并且错误信息包含详细信息")
    void should_throw_compiler_error_when_compiler_error() {
        String property = System.getProperty("user.dir");
        String parent = new File(property).getParent();
        String path = parent + "/ssh/src/main/java/com/tang/ssh/domain/entity/SshConnection.java";
        BaseException exception = Assertions.assertThrows(BaseException.class,
            () -> DynamicCompiler.compilerToClass(path));
        Assertions.assertEquals(BaseErrorCode.COMPILER_ERROR.getCode(), exception.getErrorCode().getCode());
        Assertions.assertTrue(exception.toString().contains("^"));
    }
}
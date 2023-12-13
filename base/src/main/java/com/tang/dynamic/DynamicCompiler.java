/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.dynamic;

import com.tang.exception.BaseErrorCode;
import com.tang.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 动态编译器
 * <p>
 * 在运行时编译java文件
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/13
 */
@Slf4j
public class DynamicCompiler {
    /**
     * 将java文件编译成class文件，返回对应的class文件的路径
     *
     * @param path java文件路径
     * @return class文件路径
     * @throws BaseException 编译失败
     */
    public static String compilerToClass(String path) throws BaseException {
        check(path);
        // 获取 JavaCompiler 实例
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 使用 DiagnosticCollector 来捕获编译错误和警告信息
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        // 使用自定义文件管理器
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, null,
            StandardCharsets.UTF_8)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(path);
            // 编译 Java 源代码
            Boolean result = compiler.getTask(null, fileManager, diagnosticCollector, null, null, compilationUnits)
                .call();
            if (result) {
                return path.substring(0, path.lastIndexOf(".")) + ".class";
            }
            // 处理编译错误和警告信息
            StringBuilder error = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                error.append(diagnostic.toString()).append("\n");
            }
            throw new BaseException(BaseErrorCode.COMPILER_ERROR.getCode(), BaseErrorCode.COMPILER_ERROR.getDescKey()
                , new String[]{error.toString()});
        } catch (IOException e) {
            log.error("compiler error!", e);
            throw new BaseException(BaseErrorCode.COMPILER_ERROR.getCode(), BaseErrorCode.COMPILER_ERROR.getDescKey()
                , new String[]{e.getMessage()});
        }
    }

    private static void check(String path) throws BaseException {
        if (path == null || !path.endsWith(".java")) {
            throw new BaseException(BaseErrorCode.FILE_SUFFIX_ILLEGAL);
        }
        File file = new File(path);
        if (!file.isFile()) {
            throw new BaseException(BaseErrorCode.FILE_FORMAT_ILLEGAL);
        }
    }
}
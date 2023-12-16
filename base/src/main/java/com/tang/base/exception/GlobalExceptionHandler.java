/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.exception;

import com.tang.base.response.Response;
import com.tang.base.utils.ScanClassUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 全局的异常处理拦截器
 * <p>
 * 会自动扫描com.tang包与配置文件中tang.exception.scan中指定包下的所有类，将实现了{@link IRestExceptionHandler}接口的类都注册
 * {@link GlobalExceptionHandler#handlers}异常处理器, 捕获异常后，
 * 会自动调用{@link IRestExceptionHandler#canHandle(Throwable)}判断是否能够处理异常，如果可以处理，则使用第一个找到的异常处理器处理，
 * 否则调用{@link GlobalExceptionHandler#defaultHandle(Throwable)}默认的异常处理方法
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @Value("#{'${tang.exception.scan:}'.replaceAll('\\.','/').split(',')?:new String[0]}")
    private Set<String> customPaths;

    private final List<IRestExceptionHandler> handlers = new ArrayList<>();

    /**
     * 全局异常捕获器
     *
     * @param throwable 最基准的异常
     * @return 返回给前端的响应
     */
    @ExceptionHandler(Throwable.class)
    public Response<?> handleThrowable(Throwable throwable) {
        log.error("handle request error: ", throwable);
        return handlers.stream().filter(handle -> handle.canHandle(throwable))
            .findFirst().map(handle -> handle.handle(throwable)).orElseGet(() -> this.defaultHandle(throwable));
    }

    private <T> Response<T> defaultHandle(Throwable throwable) {
        if (throwable instanceof BaseException) {
            return Response.fail((BaseException) throwable);
        }
        return Response.fail(BaseErrorCode.SYSTEM_INTERNAL_ERROR);
    }

    @PostConstruct
    private void initHandle() {
        customPaths.add("com/tang");
        log.info("custom scan paths: {}", customPaths);
        Set<String> needPaths = new HashSet<>();
        List<String> paths = customPaths.stream().sorted().toList();
        for (String customPath : paths) {
            boolean needAdd = isNeedAdd(customPath, needPaths);
            if (needAdd) {
                needPaths.add(customPath);
            }
        }
        log.info("will scan package: {}", needPaths);
        needPaths.stream().map(ScanClassUtils::scanAllClassFullName).flatMap(List::stream)
            .forEach(this::registerHandler);
    }

    private boolean isNeedAdd(String customPath, Set<String> needPaths) {
        boolean needAdd = true;
        for (String needPath : needPaths) {
            // 如果新的是以某个旧的开头
            if (customPath.startsWith(needPath)) {
                String[] customSplit = customPath.split("/");
                String[] needSplit = needPath.split("/");
                int len = needSplit.length;
                // 旧的必定比新的短, 如果新的完全被旧的包围，就不添加新的，避免重复扫描
                if (needSplit[len - 1].equals(customSplit[len - 1])) {
                    needAdd = false;
                }
            }
        }
        return needAdd;
    }

    private void registerHandler(String className) {
        try {
            Class<?> scanClass = GlobalExceptionHandler.class.getClassLoader().loadClass(className);
            if (IRestExceptionHandler.class.isAssignableFrom(scanClass) && !className.equals(
                IRestExceptionHandler.class.getName())) {
                Object instance = scanClass.getDeclaredConstructor().newInstance();
                log.info("register handle: {}", className);
                handlers.add((IRestExceptionHandler) instance);
            }
        } catch (Throwable e) {
            log.error("register handler error: {}", e.getMessage());
        }
    }
}
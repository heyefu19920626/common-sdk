/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭工具类，用于关闭各种流
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@Slf4j
public class CloseUtils {
    /**
     * 关闭流
     *
     * @param closeable 需要关闭的流
     */
    public static void close(Closeable... closeable) {
        if (closeable == null) {
            return;
        }
        for (Closeable close : closeable) {
            if (close == null) {
                continue;
            }
            try {
                close.close();
            } catch (IOException e) {
                log.error("close error!", e);
            }
        }
    }
}
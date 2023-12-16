/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 扫描类工具, 借助Spring的能力扫描指定包下的所有类
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/9
 */
@Slf4j
public class ScanClassUtils {

    /**
     * 扫描指定包路径下所有类的全路径
     *
     * @param path 指定的包路径
     * @return 该包下的所有类的全路径
     */
    public static List<String> scanAllClassFullName(String path) {
        log.info("start scan package: {}", path);
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
        try {
            Resource[] resources = pathMatchingResourcePatternResolver.getResources(
                "classpath*:" + path + "/**/*.class");
            return Arrays.stream(resources).map(resource -> ScanClassUtils.scanPath(resource,
                cachingMetadataReaderFactory)).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("scan package({}) error", path, e);
        }
        return new ArrayList<>();
    }

    private static String scanPath(Resource resource, CachingMetadataReaderFactory cachingMetadataReaderFactory) {
        try {
            MetadataReader reader = cachingMetadataReaderFactory.getMetadataReader(resource);
            return reader.getClassMetadata().getClassName();
        } catch (Throwable e) {
            log.error("scan error: {}", e.getMessage());
        }
        return "";
    }
}
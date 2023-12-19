/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 架构约束测试
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/18
 */
class ArchUnitTest {
    JavaClasses classes = new ClassFileImporter().importPackages("com.tang");

    // @Test
    @DisplayName("base下的包不能依赖任何非base下的包")
    void base_not_depend_any_package() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage("com.tang.base.context..")
            .should().dependOnClassesThat().resideInAnyPackage("com.tang..")
            // .should().dependOnClassesThat().resideInAPackage("com.tang.base..")
            .check(classes);
    }

    @Test
    @DisplayName("base.utils下的包不能依赖任何非base下的包")
    void base_utils_not_depend_any_package() {
        ArchRuleDefinition.noClasses().that().resideInAPackage("com.tang.base.utils..")
            .should().dependOnClassesThat().resideInAnyPackage("com.tang..")
            .check(classes);
    }
}
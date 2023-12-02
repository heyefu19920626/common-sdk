/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang;

import com.tang.exception.BaseErrorCode;
import com.tang.response.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 单测启动类
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/2
 */
@SpringBootApplication
@RestController
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class);
    }

    @GetMapping("test")
    public Response<String> test() {
        return Response.fail(BaseErrorCode.SYSTEM_INTERNAL_ERROR);
    }
}

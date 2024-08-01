/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.tang;

/**
 * agent测试类
 *
 * @author h00620506
 * @version [dms, 2024/8/1]
 * @since 2024/8/1
 **/
public class AgentTest {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            System.out.println("process result: " + process());
            Thread.sleep(1000);
        }
    }

    public static String process() {
        System.out.println("process!");
        return "success";
    }
}

package com.tang.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 生产者服务
 *
 * @author he
 * @since 2024-01.03-23:58
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class);
    }
}

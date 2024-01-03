package com.tang.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author he
 * @since 2024-01.04-00:18
 */
@FeignClient(name = "producer")
public interface ProducerFeign {
    @GetMapping("/producer/name")
    String name();

    @GetMapping("/consumer/name")
    String nameFor();
}
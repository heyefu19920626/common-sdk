package com.tang.consumer.controller;

import com.tang.consumer.feign.ProducerFeign;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author he
 * @since 2024-01.04-00:15
 */
@RequestMapping("consumer")
@RestController
@AllArgsConstructor
public class ConsumerController {
    private ProducerFeign producerFeign;

    @GetMapping("name")
    public String name() {
        return "consumer";
    }

    @GetMapping("name/producer")
    public String nameFrom() {
        return producerFeign.name();
    }
}

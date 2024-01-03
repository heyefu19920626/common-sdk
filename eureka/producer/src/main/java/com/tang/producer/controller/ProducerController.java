package com.tang.producer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author he
 * @since 2024-01.04-00:00
 */
@RestController
@RequestMapping("producer")
public class ProducerController {

    @GetMapping("name")
    public String name() {
        return "producer";
    }
}

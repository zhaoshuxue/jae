package com.zsx.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by highness on 2017/11/6 0006.
 */
@RestController
public class DemoController {

    @GetMapping("/")
    public String a(){
        return UUID.randomUUID().toString();
    }
}

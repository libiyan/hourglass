package com.drgou.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 健康检测
 * @Author: wendy
 * @Date: 2020/3/19
 */
@RestController
public class CheckController {
    @RequestMapping("/msg/check")
    public String check() {
        return "OK";
    }
}

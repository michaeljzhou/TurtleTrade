package com.turtle.trade.controller;

import com.turtle.trade.service.ComputeIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComputeIndexController {

    @Autowired
    private ComputeIndexService computeIndexService;

    @GetMapping("/ComputeHKIndexes")
    public String updateHKIndex() {

        computeIndexService.computeHKIndexes();

        return String.format("Successful!");
    }

    @GetMapping("/ComputeSSIndexes")
    public String updateSSIndex() {

        computeIndexService.computeSSIndexes();

        return String.format("Successful!");
    }
}

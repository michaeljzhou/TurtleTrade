package com.turtle.trade.controller;

import com.turtle.trade.entity.IndexJson;
import com.turtle.trade.mapper.IndexJsonMapper;
import com.turtle.trade.service.UpdateIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UpdateIndexController {

    @Autowired
    private UpdateIndexService updateIndexService;

    @GetMapping("/UpdateIndexes")
    public String updateIndex() {

        updateIndexService.updateIndexFromJson();

        return String.format("Successful!");
    }
}

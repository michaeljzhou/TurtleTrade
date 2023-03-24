package com.turtle.trade.controller;

import com.turtle.trade.service.FetchStockIndexService;
import com.turtle.trade.service.UpdateIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FetchIndexController {

    @Autowired
    private FetchStockIndexService fetchStockIndexService;

    @GetMapping("/FetchIndexes")
    public String updateIndex() {

        fetchStockIndexService.fetchCurrentPrice();

        return String.format("Successful!");
    }
}

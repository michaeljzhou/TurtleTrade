package com.turtle.trade.controller;

import com.turtle.trade.service.FetchStockIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FetchIndexController {

    @Autowired
    private FetchStockIndexService fetchStockIndexService;

    @GetMapping("/FetchHKIndexes")
    public String updateHKIndex() {

        fetchStockIndexService.fetchHKCurrentPrice();

        return String.format("Successful!");
    }

    @GetMapping("/FetchSSIndexes")
    public String updateSSIndex() {

        fetchStockIndexService.fetchSSCurrentPrice();

        return String.format("Successful!");
    }
}

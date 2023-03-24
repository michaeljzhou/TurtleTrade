package com.turtle.trade.controller;

import com.turtle.trade.service.StockListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StockListController {

    @Autowired
    private StockListService stockListService;

    @RequestMapping("/list")
    public String showAll(Model model, @RequestParam(required = false) String buy) {
        model.addAttribute("stocks", stockListService.findAll(buy));
        return "stockList";
    }
}

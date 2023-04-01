package com.turtle.trade.controller;

import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.entity.PO.HoldStockPO;
import com.turtle.trade.service.StockIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StockListController {

    @Autowired
    private StockIndexService stockListService;

    @RequestMapping("/")
    public String index() {
        return "redirect:/stocks_ss_indexes";
    }

    @RequestMapping("/stocks_ss_indexes")
    public String showSSAll(Model model, @RequestParam(required = false) String buy) {
        model.addAttribute("stocks", stockListService.findSSAll(buy));
        return "stocks/list";
    }

    @RequestMapping("/stocks_hk_indexes")
    public String showHKAll(Model model, @RequestParam(required = false) String buy) {
        model.addAttribute("stocks", stockListService.findHKAll(buy));
        return "stocks/hk_list";
    }
}

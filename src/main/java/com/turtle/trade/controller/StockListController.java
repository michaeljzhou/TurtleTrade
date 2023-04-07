package com.turtle.trade.controller;

import com.turtle.trade.service.CompanyCategoryService;
import com.turtle.trade.service.StockIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class StockListController {

    @Autowired
    private StockIndexService stockListService;

    @Autowired
    private CompanyCategoryService companyCategoryService;

    @RequestMapping("/")
    public String index() {
        return "redirect:/stocks_ss_indexes";
    }

    @RequestMapping("/stocks_ss_indexes")
    public String showSSAll(Model model, @RequestParam(required = false) String buy, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) String searchName, @RequestParam(required = false) String searchCode) {
        model.addAttribute("stocks", stockListService.findSSAll(buy, categoryId, searchName, searchCode));
        model.addAttribute("categories", companyCategoryService.findAll());
        model.addAttribute("buy", buy);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchCode", searchCode);
        return "stocks/list";
    }

    @RequestMapping("/stocks_hk_indexes")
    public String showHKAll(Model model, @RequestParam(required = false) String buy, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) String searchName, @RequestParam(required = false) String searchCode) {
        model.addAttribute("stocks", stockListService.findHKAll(buy, categoryId, searchName, searchCode));
        model.addAttribute("categories", companyCategoryService.findAll());
        model.addAttribute("buy", buy);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchCode", searchCode);
        return "stocks/hk_list";
    }
}

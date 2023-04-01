package com.turtle.trade.controller;

import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.service.StockIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FocusOnController {

    @Autowired
    private StockIndexService stockListService;

    @RequestMapping("/focus_list")
    public String showSSAll(Model model) {
        model.addAttribute("stocks", stockListService.findFocusAll());
        return "stocks/focus_list";
    }

    @GetMapping("/focus/{code}")
    @ResponseBody
    public String focusOn(@PathVariable("code") String code){
        CompanyIndexes companyIndexes = stockListService.get(code);
        companyIndexes.setFocusOn(!companyIndexes.getFocusOn());

        stockListService.update(companyIndexes);

        return "success";
    }
}

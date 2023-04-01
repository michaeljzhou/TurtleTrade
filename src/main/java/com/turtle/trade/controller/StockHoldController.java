package com.turtle.trade.controller;

import com.turtle.trade.entity.Company;
import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.entity.HoldStock;
import com.turtle.trade.entity.PO.HoldStockPO;
import com.turtle.trade.service.CompanyManagerService;
import com.turtle.trade.service.HoldStockService;
import com.turtle.trade.service.StockIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StockHoldController {

    @Autowired
    private HoldStockService holdStockService;

    @Autowired
    private StockIndexService stockIndexService;

    @RequestMapping("/hold_list")
    public String showAll(Model model, @RequestParam(required = false) String buy) {
        List<HoldStockPO> holdStockPOList = new ArrayList<>();
        List<HoldStock> holdStockList = holdStockService.findAll();
        for (HoldStock holdStock : holdStockList) {
            CompanyIndexes companyIndexes = stockIndexService.get(holdStock.getCode());
            HoldStockPO holdStockPO = new HoldStockPO(holdStock);
            holdStockPO.setCode(holdStock.getCode());
            holdStockPO.setName(companyIndexes.getName());
            holdStockPO.setAverageTrueRange(companyIndexes.getAverageTrueRange());
            holdStockPO.setMa10Price(companyIndexes.getMa10Price());
            holdStockPO.setMa20Price(companyIndexes.getMa20Price());
            holdStockPO.setMa55Price(companyIndexes.getMa55Price());
            holdStockPO.setLastClose(companyIndexes.getLastClose());
            holdStockPO.setShortSellSignal(companyIndexes.getShortSellSignal());
            holdStockPO.setShortMinSellSignal(companyIndexes.getShortMinSellSignal());
            holdStockPO.setLongSellSignal(companyIndexes.getLongSellSignal());
            holdStockPO.setLongMinSellSignal(companyIndexes.getLongMinSellSignal());
            BigDecimal currentAmount = holdStockPO.getBuyAmount().multiply(companyIndexes.getLastClose());
            holdStockPO.setProfitLoss(currentAmount.subtract(holdStockPO.getBuyTotalAmount()));
            holdStockPOList.add(holdStockPO);
        }
        model.addAttribute("stocks", holdStockPOList);
        return "hold/list";
    }

    @GetMapping("/hold/{code}")
    public String toEditPage(@PathVariable("code") String code, Model model){
        CompanyIndexes companyIndexes = stockIndexService.get(code);
        HoldStockPO holdStockPO = new HoldStockPO();
        holdStockPO.setCode(code);
        holdStockPO.setName(companyIndexes.getName());
        holdStockPO.setBuyPrice(companyIndexes.getLastClose());
        model.addAttribute("holdStock",holdStockPO);

        //回到修改页面(add是一个修改添加二合一的页面);
        return "hold/add";
    }

    @PostMapping("/hold")
    public String addHold(HoldStock holdStock){
        //来到类别列表页面

        System.out.println("保存的信息："+holdStock);
        CompanyIndexes companyIndexes = stockIndexService.get(holdStock.getCode());
        holdStock.setStopLossPrice(holdStock.getBuyPrice().subtract(companyIndexes.getAverageTrueRange().multiply(BigDecimal.valueOf(2))));

        holdStock.setBuyTotalAmount(holdStock.getBuyPrice().multiply(holdStock.getBuyAmount()));
        //保存类别
        holdStockService.save(holdStock);
        // redirect: 表示重定向到一个地址  /代表当前项目路径
        // forward: 表示转发到一个地址
        return "redirect:/hold_list";
    }
}

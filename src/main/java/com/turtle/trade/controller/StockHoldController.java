package com.turtle.trade.controller;

import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.entity.HoldStock;
import com.turtle.trade.entity.PO.HoldStockPO;
import com.turtle.trade.service.CompanyCategoryService;
import com.turtle.trade.service.HoldStockService;
import com.turtle.trade.service.StockIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class StockHoldController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HoldStockService holdStockService;

    @Autowired
    private StockIndexService stockIndexService;

    @Autowired
    private CompanyCategoryService companyCategoryService;

    @RequestMapping("/hold_list")
    public String showAll(Model model, @RequestParam(required = false) String buy, @RequestParam(required = false) Integer categoryId) {
        List<HoldStockPO> holdStockPOList = new ArrayList<>();
        List<HoldStock> holdStockList = holdStockService.findAll();
        Map<Integer, String> categoryMap = companyCategoryService.findAllMaps();
        for (HoldStock holdStock : holdStockList) {
            CompanyIndexes companyIndexes = stockIndexService.get(holdStock.getCode());
            HoldStockPO holdStockPO = new HoldStockPO(holdStock);
            holdStockPO.setCode(holdStock.getCode());
            holdStockPO.setName(companyIndexes.getName());
            holdStockPO.setCategoryId(companyIndexes.getCategoryId());
            holdStockPO.setCategoryName(categoryMap.get(companyIndexes.getCategoryId()));
            holdStockPO.setAverageTrueRange(companyIndexes.getAverageTrueRange());
            holdStockPO.setMa10Price(companyIndexes.getMa10Price());
            holdStockPO.setMa20Price(companyIndexes.getMa20Price());
            holdStockPO.setMa55Price(companyIndexes.getMa55Price());
            holdStockPO.setLastClose(companyIndexes.getLastClose());
            holdStockPO.setShortSellSignal(companyIndexes.getShortSellSignal());
            holdStockPO.setShortMinSellSignal(companyIndexes.getShortMinSellSignal());
            holdStockPO.setLongSellSignal(companyIndexes.getLongSellSignal());
            holdStockPO.setLongMinSellSignal(companyIndexes.getLongMinSellSignal());
            if (holdStock.getHoldFlag()) {
                BigDecimal currentAmount = holdStockPO.getBuyAmount().multiply(companyIndexes.getLastClose());
                holdStockPO.setProfitLoss(currentAmount.subtract(holdStockPO.getBuyTotalAmount()));
            }

            holdStockPOList.add(holdStockPO);
        }
        model.addAttribute("stocks", holdStockPOList);
        model.addAttribute("categories", companyCategoryService.findAll());
        model.addAttribute("buy", buy);
        model.addAttribute("categoryId", categoryId);
        return "hold/list";
    }

    @GetMapping("/hold/{code}")
    public String toBuyPage(@PathVariable("code") String code, Model model){
        CompanyIndexes companyIndexes = stockIndexService.get(code);
        HoldStockPO holdStockPO = new HoldStockPO();
        holdStockPO.setCode(companyIndexes.getCode());
        holdStockPO.setName(companyIndexes.getName());
        holdStockPO.setBuyPrice(companyIndexes.getLastClose());
        model.addAttribute("holdStock",holdStockPO);

        //回到修改页面(add是一个修改添加二合一的页面);
        return "hold/add";
    }

    @GetMapping("/hold_sell/{id}")
    public String toSellPage(@PathVariable("id") Long id, Model model){
        HoldStock holdStock = holdStockService.get(id);
        CompanyIndexes companyIndexes = stockIndexService.get(holdStock.getCode());
        HoldStockPO holdStockPO = new HoldStockPO(holdStock);
        holdStockPO.setCode(holdStock.getCode());
        holdStockPO.setName(companyIndexes.getName());
        holdStockPO.setBuyPrice(companyIndexes.getLastClose());
        model.addAttribute("holdStock",holdStockPO);

        //回到修改页面(add是一个修改添加二合一的页面);
        return "hold/sell";
    }

    @PostMapping("/hold")
    public String addHold(HoldStock holdStock){
        //来到类别列表页面
        logger.info("保存的信息："+holdStock);
        CompanyIndexes companyIndexes = stockIndexService.get(holdStock.getCode());
        holdStock.setStopLossPrice(holdStock.getBuyPrice().subtract(companyIndexes.getAverageTrueRange().multiply(BigDecimal.valueOf(2))));

        holdStock.setBuyTotalAmount(holdStock.getBuyPrice().multiply(holdStock.getBuyAmount()));
        //保存类别
        holdStockService.save(holdStock);
        // redirect: 表示重定向到一个地址  /代表当前项目路径
        // forward: 表示转发到一个地址
        return "redirect:/hold_list";
    }

    @PutMapping("/hold")
    public String updateHold(HoldStock holdStock){
        logger.info("修改的数据："+holdStock);
        HoldStock dbHoldStock = holdStockService.get(holdStock.getId());
        BigDecimal sellAmount = holdStock.getBuyAmount().multiply(holdStock.getSellPrice());
        holdStock.setProfitLoss(sellAmount.subtract(dbHoldStock.getBuyTotalAmount()));
        holdStock.setHoldFlag(Boolean.FALSE);
        holdStockService.save(holdStock);
        return "redirect:/hold_list";
    }
}

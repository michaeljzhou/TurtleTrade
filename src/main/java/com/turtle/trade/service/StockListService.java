package com.turtle.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.mapper.CompanyIndexesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Slf4j
public class StockListService {

    @Autowired
    private CompanyIndexesMapper companyIndexesMapper;

    public List<CompanyIndexes> findAll(String buy) {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(buy)) {
            if ("shortBuy".equals(buy)) {
                queryWrapper.eq("short_buy_signal", 1);
            }
            if ("longBuy".equals(buy)) {
                queryWrapper.eq("long_buy_signal", 1);
            }
        }
        queryWrapper.orderByDesc("type");
        queryWrapper.orderByDesc("short_buy_signal");
        queryWrapper.orderByDesc("long_buy_signal");

        return companyIndexesMapper.selectList(queryWrapper);
    }
}

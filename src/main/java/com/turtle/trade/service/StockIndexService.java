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
public class StockIndexService {

    @Autowired
    private CompanyIndexesMapper companyIndexesMapper;

    public List<CompanyIndexes> findSSAll(String buy) {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();

        queryWrapper.ne("type", "HK");
        if (StringUtils.hasLength(buy)) {
            if ("shortBuy".equals(buy)) {
                queryWrapper.eq("short_buy_signal", 1);
            }
            if ("shortPlusBuy".equals(buy)) {
                queryWrapper.eq("short_max_buy_signal", 1);
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

    public List<CompanyIndexes> findHKAll(String buy) {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("type", "HK");
        if (StringUtils.hasLength(buy)) {
            if ("shortBuy".equals(buy)) {
                queryWrapper.eq("short_buy_signal", 1);
            }
            if ("shortPlusBuy".equals(buy)) {
                queryWrapper.eq("short_max_buy_signal", 1);
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

    public CompanyIndexes get(String code) {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("code", code);
        return companyIndexesMapper.selectOne(queryWrapper);
    }

    public void update(CompanyIndexes companyIndexes) {
        QueryWrapper<CompanyIndexes> wrapper = new QueryWrapper<>();
        wrapper.eq("code", companyIndexes.getCode());
        companyIndexesMapper.update(companyIndexes, wrapper);
    }

    public List<CompanyIndexes> findFocusAll() {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("focus_on", 1);

        queryWrapper.orderByDesc("type");
        queryWrapper.orderByDesc("short_buy_signal");
        queryWrapper.orderByDesc("long_buy_signal");

        return companyIndexesMapper.selectList(queryWrapper);
    }
}

package com.turtle.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.mapper.CompanyIndexesMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StockIndexService {

    @Autowired
    private CompanyIndexesMapper companyIndexesMapper;

    @Autowired
    private CompanyCategoryService categoryService;

    public List<CompanyIndexes> findSSAll(String buy, Integer categoryId, String searchName, String searchCode) {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();

        queryWrapper.ne("type", "HK");

        return findAll(buy, categoryId, searchName, searchCode, queryWrapper);
    }

    private List<CompanyIndexes> findAll(String buy, Integer categoryId, String searchName, String searchCode, QueryWrapper<CompanyIndexes> queryWrapper) {
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
        if (categoryId !=null && categoryId > 0) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (StringUtils.hasLength(searchCode)) {
            queryWrapper.eq("code", searchCode);
        }
        if (StringUtils.hasLength(searchName)) {
            queryWrapper.like("name", searchName);
        }
        queryWrapper.orderByDesc("type");
        queryWrapper.orderByDesc("category_id");
        queryWrapper.orderByDesc("short_buy_signal");
        queryWrapper.orderByDesc("long_buy_signal");

        List<CompanyIndexes> companyIndexesList = companyIndexesMapper.selectList(queryWrapper);

        Map<Integer, String> categoryMap = categoryService.findAllMaps();

        for (CompanyIndexes companyIndexes : companyIndexesList) {
            companyIndexes.setCategoryName(categoryMap.get(companyIndexes.getCategoryId()));
        }

        return companyIndexesList;
    }

    public List<CompanyIndexes> findHKAll(String buy, Integer categoryId, String searchName, String searchCode) {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("type", "HK");
        return findAll(buy, categoryId, searchName, searchCode, queryWrapper);
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

    public List<CompanyIndexes> findFocusAll(String buy, Integer categoryId, String searchName, String searchCode) {
        QueryWrapper<CompanyIndexes> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("focus_on", 1);

        return findAll(buy, categoryId, searchName, searchCode, queryWrapper);
    }
}

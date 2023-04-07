package com.turtle.trade.service;

import com.turtle.trade.entity.CompanyCategory;
import com.turtle.trade.mapper.CompanyCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CompanyCategoryService {

    @Autowired
    private CompanyCategoryMapper categoryMapper;

    public Map<Integer, String> findAllMaps() {
        List<CompanyCategory> categoryList = categoryMapper.selectList(null);
        Map<Integer, String> categoryMap = new HashMap<>();
        for (CompanyCategory category : categoryList) {
            categoryMap.put(category.getId(), category.getName());
        }
        return categoryMap;
    }

    public List<CompanyCategory> findAll() {
        List<CompanyCategory> categoryList = categoryMapper.selectList(null);
        return categoryList;
    }
}

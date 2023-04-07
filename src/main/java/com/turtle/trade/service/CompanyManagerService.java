package com.turtle.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.Company;
import com.turtle.trade.mapper.CompanyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CompanyManagerService {

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyCategoryService categoryService;

    public List<Company> findAll(String searchName, String searchCode) {
        Map<Integer, String> categoryMap = categoryService.findAllMaps();

        QueryWrapper<Company> wrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(searchCode)) {
            wrapper.eq("code", searchCode);
        }
        if (StringUtils.hasLength(searchName)) {
            wrapper.like("name", searchName);
        }

        List<Company> companyList = companyMapper.selectList(wrapper);
        for (Company company : companyList) {
            company.setCategoryName(categoryMap.get(company.getCategoryId()));
        }
        return companyList;
    }

    public void delete(String code) {
        QueryWrapper<Company> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        companyMapper.delete(wrapper);
    }

    public void save(Company company) {
        companyMapper.insert(company);
    }

    public void update(Company company) {
        QueryWrapper<Company> wrapper = new QueryWrapper<>();
        wrapper.eq("code", company.getCode());
        companyMapper.update(company, wrapper);
    }

    public Company get(String code) {
        QueryWrapper<Company> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        return companyMapper.selectOne(wrapper);
    }
}

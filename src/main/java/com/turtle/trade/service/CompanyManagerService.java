package com.turtle.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.Company;
import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.entity.StockIndex;
import com.turtle.trade.mapper.CompanyIndexesMapper;
import com.turtle.trade.mapper.CompanyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Slf4j
public class CompanyManagerService {

    @Autowired
    private CompanyMapper companyMapper;

    public List<Company> findAll() {
        return companyMapper.selectList(null);
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

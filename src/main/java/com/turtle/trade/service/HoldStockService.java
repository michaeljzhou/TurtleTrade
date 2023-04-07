package com.turtle.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.Company;
import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.entity.HoldStock;
import com.turtle.trade.mapper.CompanyMapper;
import com.turtle.trade.mapper.HoldStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class HoldStockService {

    @Autowired
    private HoldStockMapper holdStockMapper;

    public List<HoldStock> findAll() {
        return holdStockMapper.selectList(null);
    }

    public void delete(String code) {
        QueryWrapper<HoldStock> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        holdStockMapper.delete(wrapper);
    }

    public void save(HoldStock holdStock) {
        QueryWrapper<HoldStock> wrapper = new QueryWrapper<>();
        wrapper.eq("code", holdStock.getCode());
        HoldStock dbHoldStock = holdStockMapper.selectOne(wrapper);
        if (dbHoldStock != null) {
            holdStockMapper.update(holdStock, wrapper);
        } else {
            holdStockMapper.insert(holdStock);
        }
    }

    public HoldStock get(String code) {
        QueryWrapper<HoldStock> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        return holdStockMapper.selectOne(wrapper);
    }

    public HoldStock get(Long id) {
        QueryWrapper<HoldStock> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        return holdStockMapper.selectOne(wrapper);
    }
}

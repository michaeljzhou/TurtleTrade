package com.turtle.trade.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.Company;
import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.entity.StockIndex;
import com.turtle.trade.mapper.CompanyIndexesMapper;
import com.turtle.trade.mapper.CompanyMapper;
import com.turtle.trade.mapper.StockIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ComputeIndexService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private StockIndexMapper stockIndexMapper;

    @Autowired
    private CompanyIndexesMapper companyIndexesMapper;


    public void computeHKIndexes() {
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "HK");
        List<Company> companyList = companyMapper.selectList(queryWrapper);
        computeIndexes(companyList);
    }

    public void computeSSIndexes() {

        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("type", "HK");
        List<Company> companyList = companyMapper.selectList(queryWrapper);
        computeIndexes(companyList);
    }

    //@Scheduled(cron = "0 15 16 ? * MON-FRI") //从周一到周五每天下午的4点15分触发
    //@Scheduled(initialDelay = 1000, fixedRate = 500000)
    private void computeIndexes(List<Company> companyList) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            for (Company company : companyList) {
                QueryWrapper<StockIndex> wrapper = new QueryWrapper<>();
                wrapper.eq("code", company.getCode());
                wrapper.orderByDesc("index_date");
                wrapper.last("limit 60");
                List<StockIndex> stockIndexList = stockIndexMapper.selectList(wrapper);

                if (stockIndexList.size() > 20) {
                    CompanyIndexes companyIndexes = getCompanyIndexes(stockIndexList, company);
                    createOrUpdateIndex(companyIndexes);
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private CompanyIndexes getCompanyIndexes(List<StockIndex> stockIndexList, Company company) {
        logger.info("company :" + company);
        CompanyIndexes companyIndexes = new CompanyIndexes();

        try {
            companyIndexes.setCode(company.getCode());
            companyIndexes.setName(company.getName());
            companyIndexes.setType(company.getType());
            StockIndex lastDayIndex = stockIndexList.get(0);
            companyIndexes.setLastClose(lastDayIndex.getClosePrice());

            List<StockIndex> ma10IndexList = stockIndexList.subList(0, 10);
            BigDecimal ma10IndexCount = ma10IndexList.stream().map(StockIndex::getClosePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ma10IndexAverage = ma10IndexCount.divide(BigDecimal.valueOf(ma10IndexList.size()), 3, RoundingMode.DOWN);
            companyIndexes.setMa10Price(ma10IndexAverage);

            List<StockIndex> ma20IndexList = stockIndexList.subList(0, 20);
            BigDecimal ma20IndexCount = ma20IndexList.stream().map(StockIndex::getClosePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ma20IndexAverage = ma20IndexCount.divide(BigDecimal.valueOf(ma20IndexList.size()), 3, RoundingMode.DOWN);
            companyIndexes.setMa20Price(ma20IndexAverage);
            BigDecimal trueRangeCount = ma20IndexList.stream().map(StockIndex::getTrueRange).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal averageTrueRange = trueRangeCount.divide(BigDecimal.valueOf(ma20IndexList.size()), 3, RoundingMode.DOWN);
            companyIndexes.setAverageTrueRange(averageTrueRange);
            // 买入信号：最后一天收盘价突破20日均价
            companyIndexes.setShortBuySignal(lastDayIndex.getClosePrice().compareTo(ma20IndexAverage) > 0);
            // 买入信号：最后一天收盘价突破20日均价一个ATR
            companyIndexes.setShortMaxBuySignal(lastDayIndex.getClosePrice().compareTo(ma20IndexAverage.add(averageTrueRange)) > 0);
            //BigDecimal ma20IndexCount = ma20IndexList.stream().map(StockIndex::getClosePrice).reduce(BigDecimal.ZERO, BigDecimal::add);

            // 卖出信号：最后一天收盘价跌破10日均价
            companyIndexes.setShortSellSignal(lastDayIndex.getClosePrice().compareTo(ma10IndexAverage) < 0);
            // 卖出信号：最后一天收盘价跌破10日均价一个ATR
            companyIndexes.setShortMinSellSignal(lastDayIndex.getClosePrice().compareTo(ma10IndexAverage.subtract(averageTrueRange)) <= 0);


            List<StockIndex> ma30IndexList = stockIndexList.subList(0, 30);
            BigDecimal ma30IndexCount = ma20IndexList.stream().map(StockIndex::getClosePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ma30IndexAverage = ma30IndexCount.divide(BigDecimal.valueOf(ma30IndexList.size()), 3, RoundingMode.DOWN);
            companyIndexes.setMa30Price(ma30IndexAverage);

            List<StockIndex> ma55IndexList = stockIndexList.subList(0, 55);
            BigDecimal ma55IndexCount = ma55IndexList.stream().map(StockIndex::getClosePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ma55IndexAverage = ma55IndexCount.divide(BigDecimal.valueOf(ma55IndexList.size()), 3, RoundingMode.DOWN);
            companyIndexes.setMa55Price(ma55IndexAverage);
            // 买入信号：最后一天收盘价突破55日均价
            companyIndexes.setLongBuySignal(lastDayIndex.getClosePrice().compareTo(ma55IndexAverage) > 0);
            // 买入信号：最后一天收盘价突破20日均价一个ATR
            companyIndexes.setLongMaxBuySignal(lastDayIndex.getClosePrice().compareTo(ma55IndexAverage.add(averageTrueRange)) > 0);
            // 卖出信号：最后一天收盘价跌破20日均价
            companyIndexes.setLongSellSignal(lastDayIndex.getClosePrice().compareTo(ma20IndexAverage) <= 0);
            // 卖出信号：最后一天收盘价跌破20日均价一个ATR
            companyIndexes.setLongMinBuySignal(lastDayIndex.getClosePrice().compareTo(ma20IndexAverage.subtract(averageTrueRange)) <= 0);

            List<StockIndex> ma60IndexList = stockIndexList.subList(0, 60);
            BigDecimal ma60IndexCount = ma60IndexList.stream().map(StockIndex::getClosePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ma60IndexAverage = ma60IndexCount.divide(BigDecimal.valueOf(ma60IndexList.size()), 3, RoundingMode.DOWN);
            companyIndexes.setMa60Price(ma60IndexAverage);

            Timestamp timestamp = new Timestamp(new Date().getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            companyIndexes.setUpdateDate(sdf.format(timestamp));
        } catch (Exception e) {
            logger.error(companyIndexes + "," + e.getMessage(), e);
        }

        return companyIndexes;
    }

    private void createOrUpdateIndex(CompanyIndexes stockIndex) {
        try {
            logger.info("company index:" + stockIndex);
            QueryWrapper<CompanyIndexes> wrapper = new QueryWrapper<>();
            wrapper.eq("code", stockIndex.getCode());
            CompanyIndexes dbStockIndex = companyIndexesMapper.selectOne(wrapper);
            if (dbStockIndex != null) {
                companyIndexesMapper.update(stockIndex, wrapper);
            } else {
                companyIndexesMapper.insert(stockIndex);
            }
        } catch (Exception e) {
            logger.error(stockIndex + "," + e.getMessage(), e);
        }
    }

    private StockIndex getStockIndex(JSONArray dayIndex, String code, int timestampIndex, int highIndex, int lowIndex, int closeIndex, BigDecimal lastClosePrice) {
        StockIndex index = new StockIndex();

        try {
            index.setCode(code);
            index.setClosePrice(new BigDecimal(dayIndex.get(closeIndex).toString()));
            BigDecimal highPrice = new BigDecimal(dayIndex.get(highIndex).toString());
            index.setHighPrice(highPrice);
            BigDecimal lowPrice = new BigDecimal(dayIndex.get(lowIndex).toString());
            index.setLowPrice(lowPrice);
            // compute TR
            BigDecimal trueRange1 = highPrice.subtract(lowPrice);
            BigDecimal trueRange2 = highPrice.subtract(lastClosePrice);
            BigDecimal trueRange3 = lowPrice.subtract(lastClosePrice);
            BigDecimal trueRange = trueRange1.max(trueRange2);
            trueRange = trueRange.max(trueRange3);
            index.setTrueRange(trueRange);
            Timestamp timestamp = new Timestamp(Long.parseLong(dayIndex.get(timestampIndex).toString()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            index.setIndexDate(sdf.format(timestamp));
        } catch (Exception e) {
            logger.error(dayIndex + "," + e.getMessage(), e);
        }
        return index;
    }

}

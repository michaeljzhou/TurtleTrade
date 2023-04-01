package com.turtle.trade.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.Company;
import com.turtle.trade.entity.IndexJson;
import com.turtle.trade.entity.StockIndex;
import com.turtle.trade.mapper.IndexJsonMapper;
import com.turtle.trade.mapper.StockIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
@Slf4j
public class UpdateIndexService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StockIndexMapper stockIndexMapper;

    @Autowired
    private IndexJsonMapper indexJsonMapper;

    @Autowired
    private FetchStockIndexService fetchStockIndexService;

    @Autowired
    private ComputeIndexService computeIndexService;

    @Scheduled(cron = "0 15 16 ? * MON-FRI") //从周一到周五每天下午的3点16分触发
    public void updateSSIndexes(){
        // thymeleaf默认就会拼串
        fetchStockIndexService.fetchSSCurrentPrice();

        computeIndexService.computeSSIndexes();
    }

    @Scheduled(cron = "0 16 16 ? * MON-FRI") //从周一到周五每天下午的4点16分触发
    public void updateHKIndexes(){
        // thymeleaf默认就会拼串
        fetchStockIndexService.fetchHKCurrentPrice();

        computeIndexService.computeHKIndexes();
    }

    public void updateIndexFromJson() {
        QueryWrapper<IndexJson> wrapper = new QueryWrapper<>();
        wrapper.isNull("update_date");
        List<IndexJson> jsonList = indexJsonMapper.selectList(wrapper);

        for (IndexJson indexJson : jsonList) {

            processIndexJson(indexJson);
        }
    }

    private void processIndexJson(IndexJson indexJson) {
        try {
            JSONObject objc = (JSONObject) JSONObject.parse(indexJson.getJsonData());
            JSONObject data = (JSONObject) objc.get("data");
            String code = data.get("symbol").toString();
            JSONArray column = (JSONArray) data.get("column");

            Iterator itr1 = column.iterator();
            int timestampIndex = 0, highIndex = 0, lowIndex = 0, closeIndex = 0;

            for (int i = 0; i < column.size(); i++) {
                if ("timestamp".equals(column.get(i))) {
                    timestampIndex = i;
                }
                if ("high".equals(column.get(i))) {
                    highIndex = i;
                }
                if ("low".equals(column.get(i))) {
                    lowIndex = i;
                }
                if ("close".equals(column.get(i))) {
                    closeIndex = i;
                }
            }
            JSONArray itemArray = (JSONArray) data.get("item");
            List<Object> indexArray = itemArray.subList(itemArray.size() - 62, itemArray.size());

            JSONArray dayIndex = (JSONArray) indexArray.get(0);
            BigDecimal lastClose = new BigDecimal(dayIndex.get(closeIndex).toString());
            for (int i = 1; i < indexArray.size(); i++) {
                dayIndex = (JSONArray) indexArray.get(i);
                StockIndex stockIndex = getStockIndex(dayIndex, code, timestampIndex, highIndex, lowIndex, closeIndex, lastClose);
                lastClose = stockIndex.getClosePrice();
                createOrUpdateIndex(stockIndex);
            }
            indexJson.setUpdateDate(new Date());
            QueryWrapper<IndexJson> wrapper = new QueryWrapper<>();
            wrapper.eq("code", indexJson.getCode());
            indexJsonMapper.update(indexJson, wrapper);
        } catch (Exception e) {
            logger.error(indexJson.getCode() + "," + e.getMessage(), e);
        }
    }

    private void createOrUpdateIndex(StockIndex stockIndex) {
        try {
            logger.info("stock index:" +stockIndex);
            QueryWrapper<StockIndex> wrapper = new QueryWrapper<>();
            wrapper.eq("code", stockIndex.getCode());
            wrapper.eq("index_date", stockIndex.getIndexDate());
            StockIndex dbStockIndex = stockIndexMapper.selectOne(wrapper);
            if (dbStockIndex != null) {
                stockIndex.setId(dbStockIndex.getId());
                stockIndexMapper.updateById(stockIndex);
            } else {
                stockIndexMapper.insert(stockIndex);
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

    public void save(IndexJson indexJson) {
        indexJsonMapper.insert(indexJson);
    }
}

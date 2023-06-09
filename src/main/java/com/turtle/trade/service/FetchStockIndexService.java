package com.turtle.trade.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.turtle.trade.entity.Company;
import com.turtle.trade.entity.StockIndex;
import com.turtle.trade.mapper.CompanyMapper;
import com.turtle.trade.mapper.StockIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class FetchStockIndexService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "https://stock.xueqiu.com/v5/stock/realtime/quotec.json?symbol=";

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private StockIndexMapper stockIndexMapper;

    public void fetchSSCurrentPrice() {
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("type", "HK");
        List<Company> companyList = companyMapper.selectList(queryWrapper);
        fetchCurrentPrice(companyList);
    }

    public void fetchHKCurrentPrice() {
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "HK");
        List<Company> companyList = companyMapper.selectList(queryWrapper);
        fetchCurrentPrice(companyList);
    }

    //@Scheduled(cron = "0 15 16 ? * MON-FRI") //从周一到周五每天下午的4点15分触发
    //@Scheduled(initialDelay = 1000, fixedRate = 500000)
    private void fetchCurrentPrice(List<Company> companyList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        /* 当前是交易日 */
        for (Company company : companyList) {

            StockIndex stockIndex = getIndexData(company);
            /* 判断当前日期是不是交易日, 如果不是，则不进行任何操作 */
            if (stockIndex != null && currentDate.equals(stockIndex.getIndexDate())) {
                createOrUpdateIndex(stockIndex);
            }
        }
    }

    private StockIndex getIndexData(Company company) {
        StockIndex stockIndex = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpGet httpGet = new HttpGet(GET_URL + company.getCode());
            httpGet.addHeader("User-Agent", USER_AGENT);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            String responseData = getResponseData(httpResponse, company);

            stockIndex = getStockIndex(responseData, company);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return stockIndex;
    }


    private void createOrUpdateIndex(StockIndex stockIndex) {
        try {
            QueryWrapper<StockIndex> wrapper = new QueryWrapper<>();
            wrapper.eq("code", stockIndex.getCode());
            wrapper.eq("index_date", stockIndex.getIndexDate());
            StockIndex dbStockIndex = stockIndexMapper.selectOne(wrapper);
            // Not existed then create, else ignored.
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

    private StockIndex getStockIndex(String response, Company company) {
        StockIndex index = new StockIndex();

        try {
            JSONObject objc = (JSONObject) JSONObject.parse(response);
            JSONObject job = (JSONObject) objc;
            JSONArray data = (JSONArray) job.get("data");
            JSONObject stock = (JSONObject) data.get(0);

            index.setCode(company.getCode());
            index.setClosePrice(new BigDecimal(stock.get("current").toString()));
            BigDecimal highPrice = new BigDecimal(stock.get("high").toString());
            index.setHighPrice(highPrice);
            BigDecimal lowPrice = new BigDecimal(stock.get("low").toString());
            index.setLowPrice(lowPrice);
            BigDecimal lastClosePrice = new BigDecimal(stock.get("last_close").toString());
            // compute TR
            BigDecimal trueRange1 = highPrice.subtract(lowPrice);
            BigDecimal trueRange2 = highPrice.subtract(lastClosePrice);
            BigDecimal trueRange3 = lowPrice.subtract(lastClosePrice);
            BigDecimal trueRange = trueRange1.max(trueRange2);
            trueRange = trueRange.max(trueRange3);
            index.setTrueRange(trueRange);
            Timestamp timestamp = new Timestamp(Long.parseLong(stock.get("timestamp").toString()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            index.setIndexDate(sdf.format(timestamp));
        } catch (Exception e) {
            logger.error(company + "," + e.getMessage(), e);
            index = null;
        }
        return index;
    }

    private String getResponseData(HttpResponse httpResponse, Company company) {
        String ret = null;
        try {
            logger.info("GET Response Status:{} ", httpResponse.getStatusLine().getStatusCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpResponse.getEntity().getContent()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            ret = response.toString();
        } catch (IOException e) {
            logger.error(company + "," + e.getMessage(), e);
        }
        return ret;
    }
}

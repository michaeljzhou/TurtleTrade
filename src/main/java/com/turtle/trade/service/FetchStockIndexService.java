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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

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

    //@Scheduled(cron = "0 15 16 ? * MON-FRI") //从周一到周五每天下午的4点15分触发
    //@Scheduled(initialDelay = 1000, fixedRate = 500000)
    public void fetchCurrentPrice() {
        List<Company> companyList = companyMapper.selectList(null);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            for (Company company : companyList) {
                HttpGet httpGet = new HttpGet(GET_URL + company.getCode());
                httpGet.addHeader("User-Agent", USER_AGENT);
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

                String responseData = getResponseData(httpResponse, company);

                StockIndex stockIndex = getStockIndex(responseData, company);

                createOrUpdateIndex(stockIndex);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void createOrUpdateIndex(StockIndex stockIndex) {
        try{
            QueryWrapper<StockIndex> wrapper = new QueryWrapper<>();
            wrapper.eq("code", stockIndex.getCode());
            wrapper.eq("index_date", stockIndex.getIndexDate());
            StockIndex dbStockIndex = stockIndexMapper.selectOne(wrapper);
            // Not existed then create, else ignored.
            if (dbStockIndex != null) {
//                stockIndex.setId(dbStockIndex.getId());
//                stockIndexMapper.updateById(stockIndex);
            } else {
                stockIndexMapper.insert(stockIndex);
            }
        } catch (Exception e) {
            logger.error(stockIndex + ","+ e.getMessage(), e);
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
            logger.error(company + ","+ e.getMessage(), e);
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
            logger.error(company + ","+ e.getMessage(), e);
        }
        return ret;
    }
}

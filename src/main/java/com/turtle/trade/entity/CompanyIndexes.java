package com.turtle.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("company_index")
public class CompanyIndexes {

    private String name;

    private String code;

    private String type;

    private BigDecimal averageTrueRange;

    private BigDecimal ma10Price;

    private BigDecimal ma20Price;

    private BigDecimal ma30Price;

    private BigDecimal ma55Price;

    private BigDecimal ma60Price;

    private BigDecimal lastClose;

    private Boolean shortBuySignal;

    private Boolean shortMaxBuySignal;

    private Boolean shortMinBuySignal;

    private Boolean longBuySignal;

    private Boolean longMaxBuySignal;

    private Boolean longMinBuySignal;

    private Boolean shortSellSignal;

    private Boolean shortMaxSellSignal;

    private Boolean shortMinSellSignal;

    private Boolean longSellSignal;

    private Boolean longMaxSellSignal;

    private Boolean longMinSellSignal;

    private String updateDate;

}

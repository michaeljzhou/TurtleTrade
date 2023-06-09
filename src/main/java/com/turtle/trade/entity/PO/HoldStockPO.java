package com.turtle.trade.entity.PO;

import com.turtle.trade.entity.CompanyIndexes;
import com.turtle.trade.entity.HoldStock;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HoldStockPO extends CompanyIndexes {

    private Long id;

    private BigDecimal buyPrice;

    private BigDecimal sellPrice;

    private BigDecimal stopLossPrice;

    private BigDecimal buyAmount;

    private BigDecimal buyTotalAmount;

    private BigDecimal profitLoss;

    private Boolean holdFlag;

    public HoldStockPO(HoldStock holdStock) {
        this.id = holdStock.getId();
        this.buyPrice = holdStock.getBuyPrice();
        this.stopLossPrice = holdStock.getStopLossPrice();
        this.buyAmount = holdStock.getBuyAmount();
        this.buyTotalAmount = holdStock.getBuyTotalAmount();
        this.holdFlag = holdStock.getHoldFlag();
        this.setProfitLoss(holdStock.getProfitLoss());
    }

    public HoldStockPO() {

    }
}

package com.turtle.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("hold_stock")
public class HoldStock {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String code;

    private BigDecimal buyPrice;

    private BigDecimal sellPrice;

    private BigDecimal stopLossPrice;

    private BigDecimal buyAmount;

    private BigDecimal buyTotalAmount;

    private BigDecimal profitLoss;

    private Boolean holdFlag;

    private String updateDate;

}

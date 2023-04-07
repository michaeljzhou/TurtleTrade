package com.turtle.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("company_category")
public class CompanyCategory {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String name;
}

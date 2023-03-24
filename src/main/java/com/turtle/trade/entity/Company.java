package com.turtle.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("company")
public class Company {

    private String name;

    private String code;

    private String type;

}

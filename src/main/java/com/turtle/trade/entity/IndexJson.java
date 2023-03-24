package com.turtle.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Michael Zhou
 * @date 2022/10/23 11:29
 */
@Data
@TableName("index_json")
public class IndexJson implements Serializable {

    private static final long serialVersionUID = 1L;
    private String code;
    private String jsonData;
    private Date updateDate;
}

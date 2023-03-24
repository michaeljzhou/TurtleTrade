package com.turtle.trade.controller;

import com.turtle.trade.entity.IndexJson;
import com.turtle.trade.mapper.IndexJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImportIndexController {

    @Autowired
    private IndexJsonMapper indexJsonMapper;

    @GetMapping("/ImportIndexes")
    public String importIndex() {
        System.out.println(("----- selectAll method test ------"));
        List<IndexJson> userList = indexJsonMapper.selectList(null);
        //Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
        return String.format("Successful!");
    }
}

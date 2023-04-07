package com.turtle.trade.controller;

import com.turtle.trade.entity.Company;
import com.turtle.trade.entity.IndexJson;
import com.turtle.trade.service.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class CompanyManagerController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CompanyManagerService companyManagerService;

    @Autowired
    private UpdateIndexService updateIndexService;

    @Autowired
    private FetchStockIndexService fetchStockIndexService;

    @Autowired
    private ComputeIndexService computeIndexService;

    @Autowired
    private CompanyCategoryService companyCategoryService;

    @RequestMapping("/company_manager")
    public String showAll(Model model, @RequestParam(required = false) String searchName, @RequestParam(required = false) String searchCode) {
        model.addAttribute("companies", companyManagerService.findAll(searchName, searchCode));
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchCode", searchCode);

        return "company/list";
    }

    //来到类别添加页面
    @GetMapping("/company")
    public String toAddPage(Model model){
        model.addAttribute("categories", companyCategoryService.findAll());
        // thymeleaf默认就会拼串
        // classpath:/templates/xxxx.html
        return "company/add";
    }

    //来到类别添加页面
    @GetMapping("/company_data")
    public String toAddDataPage(Model model){
        // thymeleaf默认就会拼串
        // classpath:/templates/xxxx.html
        return "company/addData";
    }

    @GetMapping("/company_indexes/updateSS")
    public String updateSSIndexes(Model model){
        // thymeleaf默认就会拼串
        fetchStockIndexService.fetchSSCurrentPrice();

        computeIndexService.computeSSIndexes();

        // classpath:/templates/xxxx.html
        return "redirect:/stocks_ss_indexes";
    }

    @GetMapping("/company_indexes/updateHK")
    public String updateHKIndexes(Model model){
        // thymeleaf默认就会拼串
        fetchStockIndexService.fetchHKCurrentPrice();

        computeIndexService.computeHKIndexes();

        // classpath:/templates/xxxx.html
        return "redirect:/stocks_hk_indexes";
    }

    @PostMapping("/company_data")
    public String addCompanyData(IndexJson indexJson){
        //来到类别列表页面

        logger.info("保存的信息："+indexJson);
        //保存类别
        updateIndexService.save(indexJson);
        // redirect: 表示重定向到一个地址  /代表当前项目路径
        // forward: 表示转发到一个地址
        return "redirect:/company_manager";
    }

    //来到修改页面，查出当前部门，在页面回显
    @GetMapping("/company/{code}")
    public String toEditPage(@PathVariable("code") String code, Model model){
        Company company = companyManagerService.get(code);
        model.addAttribute("company",company);
        model.addAttribute("categories", companyCategoryService.findAll());

        //回到修改页面(add是一个修改添加二合一的页面);
        return "company/add";
    }

    @PostMapping("/company")
    public String addCompany(Company company){
        //来到类别列表页面

        logger.info("保存的信息："+company);
        //保存类别
        companyManagerService.save(company);
        // redirect: 表示重定向到一个地址  /代表当前项目路径
        // forward: 表示转发到一个地址
        return "redirect:/company_manager";
    }

    @PutMapping("/company")
    public String updateCompany(Company company){
        logger.info("修改的数据："+company);
        companyManagerService.update(company);
        return "redirect:/company_manager";
    }

    // 借阅删除
    @DeleteMapping("/company/{code}")
    public String delete(@PathVariable("code") String code) {
        companyManagerService.delete(code);
        return "redirect:/company_manager";
    }
}

package com.baidu.shop.web;

import com.baidu.shop.service.TemGoodsService;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/23
 * @Version V1.0
 **/
//@Controller
//@RequestMapping("/item")
public class PageController {

    //@Autowired
    private TemGoodsService temGoodsService;

    @GetMapping("/{spuId}.html")
    public String testPage(@PathVariable(value = "spuId") Integer spuId, ModelMap map){

        Map<String, Object> spuMap = temGoodsService.getSpuById(spuId);

        map.putAll(spuMap);
        return "item";
    }

}

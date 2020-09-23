package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(tags = "es接口")
public interface ShopEsService {

    /*@ApiOperation(value = "获取商品测试")
    @GetMapping(value = "es/goodsInfo")
    Result<JSONObject> esGoodsInfo();*/

    @ApiOperation(value = "清空es中的商品数据")
    @GetMapping(value = "es/clearGoodsEs")
    Result<JSONObject> clearGoodsEs();

    @ApiOperation(value = "新增es商品数据")
    @GetMapping(value = "es/saveGoodsEs")
    Result<JSONObject> saveGoodsES();

    @ApiOperation(value = "搜索")
    @GetMapping(value = "/es/search")
    BaseResponse search(String search, Integer page);

}

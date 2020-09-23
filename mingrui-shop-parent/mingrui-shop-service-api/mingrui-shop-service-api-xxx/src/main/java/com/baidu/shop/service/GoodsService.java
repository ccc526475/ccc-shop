package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Spu 接口")
public interface GoodsService {

    @GetMapping(value = "/spu/list")
    Result<List<SpuDTO>> list(@SpringQueryMap SpuDTO spuDTO);

    @PostMapping(value = "/spu/save")
    Result<JSONObject> save(@RequestBody SpuDTO spuDTO);

    @GetMapping(value = "/spu/getSpuDetailBySpuId")
    Result<SpuDetailEntity> getSpuDetailBySpuId(@RequestParam Integer spuId);

    @GetMapping(value = "/spu/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(@RequestParam Integer spuId);

    @PutMapping(value = "/spu/save")
    Result<JSONObject> edit(@RequestBody SpuDTO spuDTO);

    @DeleteMapping(value = "/spu/del")
    Result<JSONObject> del(Integer spuId);

    @PutMapping(value = "/spu/sale")
    Result<JSONObject> sale(@RequestBody SpuDTO spuDTO);
}

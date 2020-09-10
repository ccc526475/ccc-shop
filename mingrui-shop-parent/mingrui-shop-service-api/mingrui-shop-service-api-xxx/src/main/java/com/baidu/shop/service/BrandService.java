package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "品牌接口")
public interface BrandService {

    @ApiOperation(value = "查修品牌信息")
    @GetMapping(value = "brand/getBrandInfo")
    Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    @ApiOperation(value = "新增品牌信息")
    @PostMapping(value = "brand/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌信息")
    @PutMapping(value = "brand/save")
    Result<JSONObject> update(@Validated({MingruiOperation.Update.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "删除品牌信息")
    @DeleteMapping(value = "/brand/delById")
    Result<JSONObject> delete(Integer id);

    @ApiOperation(value = "通过分类id获取品牌名称")
    @GetMapping(value = "brand/getBrandByCategory")
    Result<List<BrandEntity>> getBrandByCategory(Integer cid);

}

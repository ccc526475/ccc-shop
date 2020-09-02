package com.baidu.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @PostMapping(value = "category/add")
    @ApiOperation(value = "新增分类")
    Result<JSONObject> add(@Validated({MingruiOperation.Add.class}) @RequestBody CategoryEntity entity);

    @PutMapping(value = "category/edit")
    @ApiOperation(value = "修改分类")
    Result<JSONObject> edit(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity entity);

    @DeleteMapping(value = "category/delete")
    @ApiOperation(value = "删除分类")
    Result<JSONObject> delete(Integer id);

    @GetMapping(value = "/category/getCategoryByBrandId")
    @ApiOperation(value = "通过brandid获取category")
    Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId);
}

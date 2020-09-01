package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api(tags = "品牌接口")
public interface BrandService {

    @GetMapping(value = "brand/getBrandInfo")
    Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    @PostMapping(value = "brand/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);
}

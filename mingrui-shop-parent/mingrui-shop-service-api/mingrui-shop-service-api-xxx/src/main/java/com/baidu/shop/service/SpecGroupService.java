package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格接口")
public interface SpecGroupService {

    @ApiOperation(value = "查询规格组")
    @GetMapping(value = "/spec/list")
    Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "新增规格")
    @PostMapping(value = "/spec/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class})@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "修改规格")
    @PutMapping(value = "/spec/save")
    Result<JSONObject> update(@Validated({MingruiOperation.Update.class})@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "删除规格")
    @DeleteMapping(value = "/spec/delete")
    Result<JSONObject> delete(Integer id);

    @ApiOperation(value = "查询规格参数")
    @GetMapping(value = "/specParam/list")
    Result<List<SpecParamEntity>> specParamlist(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation(value = "新增规格参数")
    @PostMapping(value = "/specParam/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class})@RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "修改规格参数")
    @PutMapping(value = "/specParam/save")
    Result<JSONObject> update(@Validated({MingruiOperation.Update.class})@RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "删除规格参数")
    @DeleteMapping(value = "/specParam/delete")
    Result<JSONObject> del(Integer id);

}

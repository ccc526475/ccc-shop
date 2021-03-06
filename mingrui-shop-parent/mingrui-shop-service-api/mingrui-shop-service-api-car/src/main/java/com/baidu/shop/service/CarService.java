package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.Car;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
;

/**
 * @ClassName CarService
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/10/19
 * @Version V1.0
 **/
@Api(tags = "购物车接口")
public interface CarService {

    @ApiOperation(value = "新增购物车")
    @PostMapping("/car/addCar")
    Result<JSONObject> addCar(@RequestBody Car car);

}

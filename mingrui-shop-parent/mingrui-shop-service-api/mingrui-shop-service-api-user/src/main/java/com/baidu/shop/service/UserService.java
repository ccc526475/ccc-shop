package com.baidu.shop.service;


import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName UserService
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/10/13
 * @Version V1.0
 **/
@Api(tags = "用户接口")
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> register(@Validated({MingruiOperation.Add.class}) @RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验手机号或用户名唯一")
    @GetMapping(value = "user/check/{value}/{type}")
    Boolean checkNameOrPhone(@PathVariable(value = "value")String value,
                                              @PathVariable(value = "type") Integer type);
    @ApiOperation(value = "发送验证码")
    @PostMapping(value = "user/sendValidate")
    Result<JSONObject> sendValidate(@RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验验证码")
    @GetMapping(value = "user/ckeckCode")
    Result<JSONObject> codeValidate(String phone,String verificationCode);

}

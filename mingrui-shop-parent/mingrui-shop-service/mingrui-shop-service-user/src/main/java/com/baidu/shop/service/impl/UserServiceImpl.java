package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BaiduBeanUtil;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/10/13
 * @Version V1.0
 **/
@RestController
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);

        userEntity.setCreated(new Date());
        userEntity.setPassword(BCryptUtil.hashpw(userDTO.getPassword(),BCryptUtil.gensalt()));

        userMapper.insertSelective(userEntity);

        return this.setResultSuccess();
    }
}

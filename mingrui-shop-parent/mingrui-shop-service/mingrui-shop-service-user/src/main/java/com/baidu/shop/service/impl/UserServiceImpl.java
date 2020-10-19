package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.UserConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BaiduBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/10/13
 * @Version V1.0
 **/
@Slf4j
@RestController
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisRepository redisRepository;

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);

        userEntity.setCreated(new Date());
        userEntity.setPassword(BCryptUtil.hashpw(userDTO.getPassword(),BCryptUtil.gensalt()));

        userMapper.insertSelective(userEntity);

        return this.setResultSuccess();
    }

    @Override
    public Boolean checkNameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        //判断是验证用户名还是密码
        if (type == UserConstant.USERNAME_TYPE){
            criteria.andEqualTo("username",value);
        }else if(type == UserConstant.PHONE_TYPE){
            criteria.andEqualTo("phone",value);
        }

        List<UserEntity> UserList = userMapper.selectByExample(example);
        //如果查询出的数据大于0条,则说明数据已存在,验证不能通过
        Boolean check = true;
        if (UserList.size() > 0) check = false;

        return check;
    }

    @Override
    public Result<JSONObject> sendValidate(UserDTO userDTO) {
        //6位随机数
        String code = (int)((Math.random()*9 + 1) * 100000)+"";

        log.debug("手机号:{},验证码:{}",userDTO.getPhone(),code);
        //将验证码放到redis中
        boolean set = redisRepository.set(UserConstant.USER_PHONE_CODE + userDTO.getPhone(), code);
        //设置验证码在redis中存储的时间
        if(set) redisRepository.expire(UserConstant.USER_PHONE_CODE + userDTO.getPhone(),180);

      //  LuosimaoDuanxinUtil.sendSpeak(userDTO.getPhone(),code);

        return this.setResultSuccess();
    }

    //校验验证码
    @Override
    public Result<JSONObject> codeValidate(String phone, String verificationCode) {
        //通过key获取值 验证码
        String valueCode = redisRepository.get(UserConstant.USER_PHONE_CODE + phone);
        //比较前台输入的验证码与redis中的验证码是否一致
        if (!verificationCode.equals(valueCode)) return this.setResultError("验证码错误");

        return this.setResultSuccess();
    }

}

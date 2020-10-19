package com.baidu.shop.web;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.service.UserOauthService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/10/15
 * @Version V1.0
 **/
@RestController
public class UserOauthController extends BaseApiService {

    @Autowired
    private UserOauthService service;

    @Autowired
    @Qualifier("jwtConfig")
    private JwtConfig jwtConfig;

    @PostMapping("/oauth/login")
    public Result<JsonObject> login(@RequestBody UserEntity userEntity,
                    HttpServletRequest request, HttpServletResponse response){

        String token = service.login(userEntity, jwtConfig);
        if (ObjectUtil.isNull(token)){
            return this.setResultError(HTTPStatus.USER_USER_PASSWORD_ERROR,"用户名或密码错误");
        }

        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,
                jwtConfig.getCookieMaxAge(),true);

        return this.setResultSuccess();
    }

    @GetMapping("oauth/verify")    //从cookie中获取值  value=cookie的属性名
    public Result<JsonObject> verify(@CookieValue(value = "MRSHOP_TOKEN") String token,
                       HttpServletRequest request,HttpServletResponse response){
        try {
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //解析token,证明用是登录状态,  重新刷新token
            String newToken = JwtUtils.generateToken(infoFromToken, jwtConfig.getPrivateKey(), jwtConfig.getExpire());

            //将新token写入cookie,延长过期时间
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),
                    newToken, jwtConfig.getCookieMaxAge());

            return this.setResultSuccess(infoFromToken);
        } catch (Exception e) {

            //如果有异常 说明token有问题
            //e.printStackTrace();
            //应该新建http状态为用户验证失败,状态码为403
            return this.setResultError(HTTPStatus.TOKEN_ERROR,"用户失效");
        }
    }

}

package com.baidu.shop.config;

import com.baidu.shop.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.*;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @ClassName JwtConfig
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/10/15
 * @Version V1.0
 **/
@Configuration
@Data
public class JwtConfig {

    // 密钥
    @Value("${mrshop.jwt.secret}")
    private String secret;
    // 公钥
    @Value("${mrshop.jwt.pubKeyPath}")
    private String pubKeyPath;
    // 私钥
    @Value("${mrshop.jwt.priKeyPath}")
    private String priKeyPath;
    // token有效时间
    @Value("${mrshop.jwt.expire}")
    private int expire;
    // cookie名称
    @Value("${mrshop.jwt.cookieName}")
    private String cookieName;
    // cookie有效时间
    @Value("${mrshop.jwt.cookieMaxAge}")
    private int cookieMaxAge;

    //公钥
    private PublicKey publicKey;
    //私钥
    private PrivateKey privateKey;

    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    /**
     * 先执行构造函数
     * 在执行属性注入
     * Post**
     * Constructor -> Autowired ->Post..
     */
    @PostConstruct
    public void init(){
        File pubKey = new File(pubKeyPath);
        File priKey = new File(priKeyPath);

        try {
            //生成公钥和私钥
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
            //获取 公钥或私钥
            if (!pubKey.exists() || !priKey.exists()) {
                // 生成公钥和私钥
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            logger.debug("初始化公钥和私钥失败",e);
            throw new RuntimeException();
        }

    }
}

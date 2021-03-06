package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @ClassName RunZullServerApplication
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/28
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class
RunZullServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunZullServerApplication.class);
    }

}

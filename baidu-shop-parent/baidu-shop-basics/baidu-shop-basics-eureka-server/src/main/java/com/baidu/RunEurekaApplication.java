package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName RunEurekaApplication
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/27
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaServer
public class RunEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunEurekaApplication.class);
    }

}

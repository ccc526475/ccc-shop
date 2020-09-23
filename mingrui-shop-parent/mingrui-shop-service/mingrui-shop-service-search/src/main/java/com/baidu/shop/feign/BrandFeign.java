package com.baidu.shop.feign;


import com.baidu.shop.service.BrandService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(contextId = "brandFeign",value = "xxx-service")
public interface BrandFeign extends BrandService {
}

package com.baidu.shop.feign;

import com.baidu.shop.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(contextId = "categoryFeign",value = "xxx-service")
public interface CategoryFeign extends CategoryService {
}

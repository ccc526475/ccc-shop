package com.baidu.shop.feign;

import com.baidu.shop.service.SpecGroupService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName SpecificationFeign
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/17
 * @Version V1.0
 **/
@FeignClient(contextId = "SpecificationFeign",value = "xxx-service")
public interface SpecificationFeign extends SpecGroupService {
}

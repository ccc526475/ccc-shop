package com.baidu.shop.feign;

import com.baidu.shop.service.TemplateService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName TemplateFeign
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/25
 * @Version V1.0
 **/
@FeignClient(value = "template-server")
public interface TemplateFeign extends TemplateService {
}

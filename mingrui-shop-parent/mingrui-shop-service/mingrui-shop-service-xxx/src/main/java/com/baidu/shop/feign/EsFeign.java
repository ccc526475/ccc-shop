package com.baidu.shop.feign;

import com.baidu.shop.service.ShopEsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName ShopEsFeign
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/27
 * @Version V1.0
 **/
@FeignClient(value = "search-service")
public interface EsFeign extends ShopEsService {
}

package com.baidu.shop.service;

import com.baidu.shop.dto.SpuDTO;

import java.util.Map;

/**
 * @ClassName TemGoodsService
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/23
 * @Version V1.0
 **/
public interface TemGoodsService {

    Map<String, Object> getSpuById(Integer spuId);
}

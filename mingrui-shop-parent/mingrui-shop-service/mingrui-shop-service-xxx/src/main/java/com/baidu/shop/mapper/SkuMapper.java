package com.baidu.shop.mapper;

import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.StockEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName SkuMapper
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/8
 * @Version V1.0
 **/
public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {
    @Select(value = "select s1.*,stock from tb_sku s1,tb_stock s2 where s1.id = s2.sku_id and s1.spu_id=#{spuId}")
    List<SkuDTO> getSkuBySpuId(Integer spuId);
}

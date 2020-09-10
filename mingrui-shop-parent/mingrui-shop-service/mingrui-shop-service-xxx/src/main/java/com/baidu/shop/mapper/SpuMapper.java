package com.baidu.shop.mapper;

import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuMapper extends Mapper<SpuEntity> {

    @Select(value = "SELECT\n" +
            "\ts.*,\n" +
            "\tGROUP_CONCAT( c.NAME SEPARATOR '/' ) AS categoryName,\n" +
            "\tb.`name` AS brandName \n" +
            "FROM\n" +
            "\ttb_category c,\n" +
            "\ttb_spu s,\n" +
            "\ttb_brand b \n" +
            "WHERE\n" +
            "\ts.brand_id = b.id \n" +
            "\tAND c.id IN ( s.cid1, s.cid2, s.cid3 ) \n" +
            "\tAND s.id = #{id};")
    SpuDTO getCateBrandBySpuId(Integer id);

    List<SpuDTO> getSpuDTO(SpuDTO spuDTO);

    Integer count(SpuDTO spuDTO);
}

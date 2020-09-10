package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<BrandEntity> {

    @Select("select b.`name` from tb_category_brand cb,tb_brand b where cb.brand_id=b.id and cb.category_id=#{id}")
    List<BrandEntity> getBrandByCategoryId(Integer id);

    @Select(value = "select * from tb_brand where id in (select brand_id from tb_category_brand where tb_category_brand.category_id = #{cid})")
    List<BrandEntity> getBrandByCategory(Integer cid);
}

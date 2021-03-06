package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.TemGoodsService;
import com.baidu.shop.utils.BaiduBeanUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/23
 * @Version V1.0
 **/
//@Service
public class TemGoodsServiceImpl implements TemGoodsService {

    //@Resource
    private GoodsFeign goodsFeign;

    //@Resource
    private SpecificationFeign specificationFeign;

    @Override
    public Map<String, Object> getSpuById(Integer spuId) {
        Map<String, Object> map = new HashMap<>();

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        //通过spuid查询spu
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.list(spuDTO);
        //只能返回一条数据
        if (spuInfoResult.getCode() == 200){
            SpuDTO spuInfo = spuInfoResult.getData().get(0);
            if (spuInfoResult.getData().size() == 1) {
                map.put("spuInfo",spuInfo);

                //通过spuId查询spuDetail
                Result<SpuDetailEntity> spuDetailEntity = goodsFeign.getSpuDetailBySpuId(spuId);
                if (spuDetailEntity.getCode() == 200) map.put("spuDetailEntity", spuDetailEntity.getData());

                //通过spuId查询sku
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
                if (skuResult.getCode() == 200) map.put("skus", skuResult.getData());

                //通过cid3查询 特有属性
                map.put("specParamMap",this.getSpecParamMap(spuInfo));

                //查询规格组和规格参数
               this.getGroupAndParams(spuInfo,map);
            }
        }
        return map;
    }

    private Map<Integer, String> getSpecParamMap(SpuDTO spuInfo){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuInfo.getCid3());
        specParamDTO.setGeneric(false);
        Map<Integer, String> specParamMap = new HashMap<>();
        Result<List<SpecParamEntity>> listResult = specificationFeign.specParamlist(specParamDTO);
        if (listResult.getCode()==200) {
            listResult.getData().forEach(specParam->{
                specParamMap.put(specParam.getId(),specParam.getName());
            });
        }
        return specParamMap;
    }

    private void getGroupAndParams(SpuDTO spuInfo,Map<String, Object> map){
        List<SpecParamEntity> specParams = new ArrayList<>();

        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(spuInfo.getCid3());
        Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.list(specGroupDTO);
        if (specGroupResult.getCode() == 200){
            List<SpecGroupEntity> SpecGroupList = specGroupResult.getData();
            //通过规格组id查询规格参数
            SpecParamDTO paramDTO = new SpecParamDTO();

            List<SpecGroupDTO> specGroupList = SpecGroupList.stream().map(specGroup -> {
                SpecGroupDTO groupDTO = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);
                paramDTO.setGroupId(specGroup.getId());
                paramDTO.setGeneric(true);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.specParamlist(paramDTO);

                if (specParamResult.getCode() == 200) {
                    List<SpecParamEntity> specParamList = specParamResult.getData();
                    groupDTO.setSpecParamList(specParamList);

                    if (specParamList.size()>0)specParamList.forEach(s ->specParams.add(s));
                }
                return groupDTO;
            }).collect(Collectors.toList());
            map.put("specParams",specParams);
            map.put("specGroupList",specGroupList);
        }
    }
}

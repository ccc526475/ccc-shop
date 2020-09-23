package com.baidu.shop.response;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseResponse
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/21
 * @Version V1.0
 **/
@NoArgsConstructor
@Data
public class BaseResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<CategoryEntity> categoryList;

    private List<BrandEntity> brandList;

    Map<String, List<String>> specParamAndValueMap;

    public BaseResponse(Long total, Long totalPage, List<CategoryEntity> categoryList,
                        List<BrandEntity> brandList,List<GoodsDoc> goodsDocs,
                        Map<String, List<String>> specParamAndValueMap) {
        super(HTTPStatus.OK,HTTPStatus.OK+"",goodsDocs);
        this.total = total;
        this.totalPage = totalPage;
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specParamAndValueMap = specParamAndValueMap;
    }
}

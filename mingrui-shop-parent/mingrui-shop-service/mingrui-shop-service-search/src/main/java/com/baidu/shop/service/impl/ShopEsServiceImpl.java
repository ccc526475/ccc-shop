package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.BaseDTO.BaseSearch;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.response.BaseResponse;
import com.baidu.shop.service.ShopEsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopEsServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ShopEsServiceImpl extends BaseApiService implements ShopEsService {

    @Resource
    private GoodsFeign goodsFeign;

    @Resource
    private SpecificationFeign specificationFeign;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private BrandFeign brandFeign;

    @Resource
    private CategoryFeign categoryFeign;
    
    /*
        搜索
     */
    @Override
    public BaseResponse search(BaseSearch baseSearch) {
        String filterStr = baseSearch.getFilterStr();
        Integer page = baseSearch.getPage();
        String search = baseSearch.getSearch();

        //判断搜索内容是否为空
        if (StringUtil.isEmpty(search)) throw new RuntimeException("查询内容不能为空");

        //构建查询条件
        NativeSearchQueryBuilder queryBuilder= this.getSearch(search, page,filterStr);

        //查询
        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

        //分词 设置高亮  highLightHit 下有 content id score sortValue highlightFields高亮下有 key"title" value"size=1"
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(searchHits.getSearchHits());
        //遍历 highLightHit下content 返回每条数据集合
        List<GoodsDoc> goodsDocs = highLightHit.stream().map(hit -> hit.getContent()).collect(Collectors.toList());

        //查询总条数 转换成double 计算出页数
        long total = searchHits.getTotalHits();
        double td = Long.valueOf(total).doubleValue();
        long totalPage = Double.valueOf(Math.ceil(td / 10)).longValue();

        Aggregations aggregations = searchHits.getAggregations();

        //通过品牌id String类型的集合去查询数据
        List<BrandEntity> brandList = this.getBrandList(aggregations);

        //通过分类id String类型的集合去查询数据
        List<CategoryEntity> categoryList = null;
        Integer hotCid = null;

        Map<Integer, List<CategoryEntity>> categoryMap = this.getCategoryList(aggregations);
        for (Map.Entry<Integer, List<CategoryEntity>> entry : categoryMap.entrySet()){
            categoryList =  entry.getValue();
            hotCid = entry.getKey();
        }

        //通过cid查询规格参数以及值
        Map<String, List<String>> specParamAndValueMap = this.getSpecParam(hotCid, search);

        return new BaseResponse(total,totalPage,categoryList,brandList,goodsDocs,specParamAndValueMap);
    }

    private Map<String, List<String>> getSpecParam(Integer cid,String search){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(cid);
        specParamDTO.setSearching(true);
        //通过cid获取规格参数数据
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.specParamlist(specParamDTO);
        if (specParamResult.getCode() == 200){

            List<SpecParamEntity> specParamList = specParamResult.getData();

            //聚合查询
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            //分页至少查询一条  (此处没意义)
            searchQueryBuilder.withPageable(PageRequest.of(0,1));

            specParamList.stream().forEach(specParam -> {
                searchQueryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword"));
            });

            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(searchQueryBuilder.build(), GoodsDoc.class);

            //返回参数名 和 参数值
            Map<String, List<String>> map = new HashMap<>();
            Aggregations aggregations = searchHits.getAggregations();

            specParamList.stream().forEach(specParam -> {
                Terms terms = aggregations.get(specParam.getName());
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                //参数值集合
                List<String> valueList = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
                map.put(specParam.getName(),valueList);

            });
            return map;
        }
        return null;
    }

    /*
        构建查询条件
     */
    private NativeSearchQueryBuilder getSearch(String search,Integer page,String filterStr){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        if (StringUtil.isNotEmpty(filterStr) && filterStr.length() > 2){

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> filterMap = JSONUtil.toMapValueString(filterStr);

            filterMap.forEach((k,v) ->{
                MatchQueryBuilder matchQueryBuilder = null;
                //分类/品牌 和规格参数查询方式不一样
                if (k.equals("cid3") || k.equals("brandId")){
                    matchQueryBuilder = QueryBuilders.matchQuery(k, v);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." + k + ".keyword",v);
                }
                boolQueryBuilder.must(matchQueryBuilder);
            });
            queryBuilder.withFilter(boolQueryBuilder);
        }

        //分页
        queryBuilder.withPageable(PageRequest.of(page-1,10));

        //多字段同时查询
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));
        queryBuilder.addAggregation(AggregationBuilders.terms("cid_agg").field("cid3"));

        //社设置高亮字段
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        return  queryBuilder;
    }

    /*
        通过品牌id 查询品牌数据集合
     */
    private List<BrandEntity> getBrandList(Aggregations aggregations){
        Terms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        List<String> brandList = brandBuckets.stream().map(brandBucket -> brandBucket.getKeyAsString()).collect(Collectors.toList());
        String brandIdStr = String.join(",", brandList);
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIdlist(brandIdStr);
        return brandResult.getData();
    }

    /*
        通过分类id 查询分类数据集合
     */
    private Map<Integer, List<CategoryEntity>> getCategoryList(Aggregations aggregations){

        Map<Integer, List<CategoryEntity>> map = new HashMap<>();

        Terms cid_agg = aggregations.get("cid_agg");

        List<? extends Terms.Bucket> cidBuckets = cid_agg.getBuckets();

        List<Integer> hotCid = Arrays.asList(0);

        List<Long> maxCount = Arrays.asList(0L);

        List<String> cidList = cidBuckets.stream().map(cidBucket -> {

            if (maxCount.get(0) < cidBucket.getDocCount()){
                maxCount.set(0,cidBucket.getDocCount());
                hotCid.set(0,cidBucket.getKeyAsNumber().intValue());
            }

            return cidBucket.getKeyAsString();
        }).collect(Collectors.toList());

        String cidStr = String.join(",", cidList);

        Result<List<CategoryEntity>> categoryList = categoryFeign.getCateByIdList(cidStr);

        map.put(hotCid.get(0),categoryList.getData());

        return map;
    }

    /*
        删除索引
     */
    @Override
    public Result<JSONObject> clearGoodsEs() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (indexOperations.exists()) indexOperations.delete();

        return this.setResultSuccess();
    }

    /*
       创建索引
     */
    @Override
    public Result<JSONObject> saveGoodsES(Integer spuId) {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        //创建索引
        if (!indexOperations.exists()){
            boolean b = indexOperations.create();
            indexOperations.createMapping();
            if (b) log.info("索引创建成功");
        }
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuId);
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }
    //rabbitmq add es
    @Override
    public Result<JSONObject> saveData(Integer spuId) {

        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuId);
        GoodsDoc goodsDoc = goodsDocs.get(0);
        elasticsearchRestTemplate.save(goodsDoc);
        return this.setResultSuccess();
    }

    private List<GoodsDoc> esGoodsInfo(Integer spuId) {

        SpuDTO spuDTO = new SpuDTO();
        if (spuId != 0){
            spuDTO.setId(spuId);
        }
        Result<List<SpuDTO>> spuInfo = goodsFeign.list(spuDTO);

        //查询出来的数据是多个spu
        List<GoodsDoc> goodsDocs = new ArrayList<>();

        if (spuInfo.getCode() == HTTPStatus.OK){

            List<SpuDTO> spuList = spuInfo.getData();

            spuList.stream().forEach(spu -> {

                GoodsDoc goodsDoc = new GoodsDoc();

                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setCreateTime(spu.getCreateTime());

                //通过spuId查询sku
                Map<List<Long>, List<Map<String, Object>>> skusAndPriceList = this.getSkusAndPriceList(spu);
                skusAndPriceList.forEach((k,v) ->{
                    goodsDoc.setPrice(k);
                    goodsDoc.setSkus(JSONObject.toJSONString(v));
                });
                //通过cid3查询规格参数
                Map<String, Object> specMap = this.getSpecMap(spu);

                goodsDoc.setSpecs(specMap);
                goodsDocs.add(goodsDoc);
            });

        }

        return goodsDocs;
    }

    /*
        获取 skus & 价格集合
     */
    private Map<List<Long>, List<Map<String, Object>>> getSkusAndPriceList(SpuDTO spu){

        Map<List<Long>, List<Map<String, Object>>> skusPriceListMap = new HashMap<>();
        //通过spuId获得skus
        Result<List<SkuDTO>> skuInfo = goodsFeign.getSkuBySpuId(spu.getId());
        List<Long> priceList = new ArrayList<>();
        List<Map<String, Object>> skuListMap = null;

        if (skuInfo.getCode() == HTTPStatus.OK){

            List<SkuDTO> skuList = skuInfo.getData();
            skuListMap = skuList.stream().map(sku -> {

                Map<String, Object> map = new HashMap<>();

                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("price", sku.getPrice());
                map.put("images", sku.getImages());

                priceList.add(sku.getPrice().longValue());

                return map;
            }).collect(Collectors.toList());
        }
        skusPriceListMap.put(priceList,skuListMap);
        return skusPriceListMap;

    }

    /*
        获取参数 & 参数值
     */
    private Map<String, Object> getSpecMap(SpuDTO spuDTO){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuDTO.getCid3());
        Map<String, Object> specMap = new HashMap<>();
        Result<List<SpecParamEntity>> specParamResult  = specificationFeign.specParamlist(specParamDTO);

        if (specParamResult.getCode() == HTTPStatus.OK){

            //只有规格参数的id和规格参数的名字
            List<SpecParamEntity> paramList = specParamResult.getData();
            //通过spuid去查询spuDetail,detail里面有通用和特殊规格参数的值
            Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuDTO.getId());
            if (spuDetailResult.getCode() == HTTPStatus.OK){
                SpuDetailEntity spuDetailInfo= spuDetailResult.getData();
                //通用规格参数的值
                String genericSpec = spuDetailInfo.getGenericSpec();
                Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpec);

                //特有规格参数的值
                String specialSpec = spuDetailInfo.getSpecialSpec();
                Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpec);

                paramList.stream().forEach(param ->{
                    //判断是否是sku通用属性
                    if (param.getGeneric()){
                        //判断是否是数字类型参数,处理区域查询
                        if (param.getNumeric()){
                            specMap.put(param.getName(),this.chooseSegment(genericSpecMap.get(param.getId()+""),param.getSegments(),param.getUnit()));
                        }else{
                            specMap.put(param.getName(),genericSpecMap.get(param.getId()+""));
                        }
                    }else{
                        //param.getId() 对应参数值 Detail.SpecialSpec
                        specMap.put(param.getName(),specialSpecMap.get(param.getId() + ""));
                    }
                });
            }

        }
        return specMap;
    }


    /**
     * 把具体的值转换成区间-->不做范围查询
     * @param value
     * @param segments
     * @param unit
     * @return
     */
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }


}

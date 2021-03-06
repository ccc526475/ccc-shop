package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.component.MrRabbitMQ;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.EsFeign;
import com.baidu.shop.feign.TemplateFeign;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private TemplateFeign templateFeign;

    /*@Resource
    private EsFeign esFeign;*/

    @Resource
    private MrRabbitMQ mrRabbitMQ;

    @Override
    @Transactional
    public Result<List<SpuDTO>> list(SpuDTO spuDTO) {
        //处理下分页信息,前台每次传来的 1,5  2,5  3,5............,所有得处理下
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            spuDTO.setPage((spuDTO.getPage()-1) * spuDTO.getRows());
        }
        List<SpuDTO> listDTO = spuMapper.getSpuDTO(spuDTO);

        Integer total = spuMapper.count(spuDTO);

        return this.setResult(HTTPStatus.OK,"" + total, listDTO);

      /*  //分页
        if (ObjectUtil.isNotNull(spuDTO.getPage())&& ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        //排序
        Example example = new Example(SpuEntity.class);
        if (ObjectUtil.isNotNull(spuDTO.getSort())) example.setOrderByClause(spuDTO.getOrderByClause());
        //构建条件查询
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(spuDTO.getTitle()))  criteria.andLike("title","%"+spuDTO.getTitle()+"%");
        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);
        //遍历返回查询品牌名,和类型
        List<SpuDTO> collect = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
            SpuDTO spuDTO2 = spuMapper.getCateBrandBySpuId(spuEntity.getId());
            spuDTO1.setBrandName(spuDTO2.getBrandName());
            spuDTO1.setCategoryName(spuDTO2.getCategoryName());
            return spuDTO1;
        }).collect(Collectors.toList());
        //获取分页数据
        PageInfo<SpuEntity> PageInfo = new PageInfo<>(spuEntities);
        return setResult(HTTPStatus.OK,PageInfo.getTotal()+"",collect);*/
    }

    //add 静态页面  add es
    @Override
    public Result<JSONObject> save(SpuDTO spuDTO) {
      //获取代理对象
        GoodsServiceImpl proxyObj  = (GoodsServiceImpl)AopContext.currentProxy();
        Integer spuId = proxyObj.add(spuDTO);
      /*  //调用模板接口
        if (ObjectUtil.isNotNull(spuId)) {
            Result<JSONObject> staticHTMLTemplate = templateFeign.createStaticHTMLTemplate(spuId);
            System.out.println(staticHTMLTemplate);
        }*/

      /* //将新增数据添加到es
        esFeign.saveGoodsES(spuId);*/

        mrRabbitMQ.send(spuId+"", MqMessageConstant.SPU_ROUT_KEY_SAVE);

        return this.setResultSuccess();
    }

    @Transactional
    public Integer add(SpuDTO spuDTO){
        Date date = new Date();
        // spu add
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuMapper.insertSelective(spuEntity);

        Integer spuId = spuEntity.getId();

        // detail add
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        spuDetail.setSpuId(spuId);
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailMapper.insertSelective(spuDetailEntity);

        //sku add
        this.skuSave(spuDTO.getSkus(),spuId,date);

        return spuId;
    }


    @Override
    @Transactional
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    @Transactional
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {
        List<SkuDTO> skuList = skuMapper.getSkuBySpuId(spuId);
        return this.setResultSuccess(skuList);
    }

    @Override
    @Transactional
    public Result<JSONObject> edit(SpuDTO spuDTO) {

        Date date = new Date();

        //spu edit
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //spudetail edit
        spuDetailMapper.updateByPrimaryKeySelective( BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));

        //sku del
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuDTO.getId());
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);

        List<Long> skuIDList = skuEntities.stream().map(sku -> sku.getId()).collect(Collectors.toList());

            skuMapper.deleteByIdList(skuIDList);
            //stock del
            stockMapper.deleteByIdList(skuIDList);

        //sku and stock add
        this.skuSave(spuDTO.getSkus(),spuDTO.getId(),date);

        mrRabbitMQ.send(spuDTO.getId()+"",MqMessageConstant.SPU_ROUT_KEY_UPDATE);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> del(Integer spuId) {

        //spu del
        spuMapper.deleteByPrimaryKey(spuId);
        //spuDetail del
        spuDetailMapper.deleteByPrimaryKey(spuId);
        //sku and stock del
        this.skuAndStockDel(spuId);

        /*//删除静态html
        File file = new File("D:\\six\\shop-vue\\static-html\\item\\" + spuId + ".html");
        if (file.exists()){
            file.delete();
        }*/

        //mq删除
        mrRabbitMQ.send(spuId+"",MqMessageConstant.SPU_ROUT_KEY_DELETE);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> sale(SpuDTO spuDTO) {
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        if (spuDTO.getSaleable() == 1){
            spuEntity.setSaleable(0);
        }else {
            spuEntity.setSaleable(1);
        }
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        return this.setResultSuccess();
    }

    //sku and stock del
    private void skuAndStockDel(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuList = skuMapper.selectByExample(example);
        List<Long> skuIdList = skuList.stream().map(sku -> sku.getId()).collect(Collectors.toList());

        if (!skuIdList.isEmpty()){
            skuMapper.deleteByIdList(skuIdList);
            stockMapper.deleteByIdList(skuIdList);
        }
    }

    //sku and stock add
    private void skuSave(List<SkuDTO> skus,Integer spuId,Date date){
        //sku add
        skus.stream().forEach(sku ->{
            sku.setSpuId(spuId);
            sku.setCreateTime(date);
            sku.setLastUpdateTime(date);
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(sku, SkuEntity.class);
            skuMapper.insertSelective(skuEntity);

            // stock add
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(sku.getStock());
            stockMapper.insertSelective(stockEntity);

        });
    }
}

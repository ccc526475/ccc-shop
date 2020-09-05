package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
@Transactional
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {
        //分页
        PageHelper.startPage(brandDTO.getPage(), brandDTO.getRows());

        //排序
        Example example = new Example(BrandEntity.class);
        if (StringUtil.isNotEmpty(brandDTO.getSort()))  example.setOrderByClause(brandDTO.getOrderByClause());

        //条件查询
        if(StringUtil.isNotEmpty(brandDTO.getName())) example.
                createCriteria().andLike("name","%"+brandDTO.getName()+"%");

        //查询
        List<BrandEntity> brandEntities = brandMapper.selectByExample(example);

        //数据封装
        PageInfo<List<BrandEntity>> pageInfo = new PageInfo(brandEntities);

        //返回
        return this.setResultSuccess(pageInfo);
    }

    @Override
    public Result<JSONObject> save(BrandDTO brandDTO) {
        //获取到品牌名称
        //获取到品牌名称第一个字符
        //将第一个字符转换为pinyin
        //获取拼音的首字母
        //统一转为大写
        char c = brandDTO.getName().charAt(0);
        String s = String.valueOf(c);
        String upperCase = PinyinUtil.getUpperCase(s, PinyinUtil.TO_FIRST_CHAR_PINYIN);
        char c1 = upperCase.charAt(0);
        brandDTO.setLetter(c1);

        //新增品牌并且可以返回主键
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //新增
        brandMapper.insertSelective(brandEntity);

        //新增关系表中的数据
        this.addCategoryBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> update(BrandDTO brandDTO) {

        //复制dto到entity
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        //获取name首字母,并赋值
        char c = brandDTO.getName().charAt(0);
        String upperCase = PinyinUtil.getUpperCase(String.valueOf(c), PinyinUtil.TO_FIRST_CHAR_PINYIN);
        char c1 = upperCase.charAt(0);
        brandEntity.setLetter(c1);
        //删除关系表中的数据
        this.delCategoryBrand(brandDTO.getId());
        //关系表新增数据
        this.addCategoryBrand(brandDTO,brandEntity);
        //修改
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> delete(Integer id) {
        this.delCategoryBrand(id);
        brandMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    //删除关系表中的数据
    private void delCategoryBrand(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }
    //关系表新增数据
    private void addCategoryBrand(BrandDTO brandDTO,BrandEntity brandEntity){
        if (brandDTO.getCategory().contains(",")){
            //通过split方法分割字符串,Arrays.asList()将Array转换为List,使用jdk1.8的stream
            //使用map函数返回一个新的数据,collect转换集合为stream<>,collectors.toList() 将集合转换为List
            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(
                    brandDTO.getCategory().split(","))
                    .stream().map(cid -> {

                        CategoryBrandEntity entity = new CategoryBrandEntity();
                        entity.setCategoryId(StringUtil.toInteger(cid));
                        entity.setBrandId(brandEntity.getId());

                        return entity;
                    }).collect(Collectors.toList());

            categoryBrandMapper.insertList(categoryBrandEntities);
        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }

}

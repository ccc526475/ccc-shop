package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.status.HTTPStatus;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/27
 * @Version V1.0
 **/
@Transactional
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private SpuMapper spuMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        //return this.setResultSuccess(list);
        return new Result<List<CategoryEntity>>(HTTPStatus.OK,"a",list);
    }

    @Override
    public Result<JSONObject> add(CategoryEntity entity) {

        CategoryEntity parentEntity = new CategoryEntity();
        parentEntity.setId(entity.getParentId());
        parentEntity.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(parentEntity);

        categoryMapper.insertSelective(entity);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> edit(CategoryEntity entity) {
        categoryMapper.updateByPrimaryKeySelective(entity);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delete(Integer id) {

        //判断id是否有效
        if (null == id) return this.setResultError("id为空,删除失败");

        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

        //判断是否是父节点
        if (categoryEntity.getIsParent() == 1) return this.setResultError("不能删除父节点");

        //如果分类被商品绑定 就不能被删除
        StringBuilder spuMsg = new StringBuilder();
        Example spuExample = new Example(SpuEntity.class);
        spuExample.createCriteria().andEqualTo("cid3",id);
        List<SpuEntity> spuList = spuMapper.selectByExample(spuExample);
        if (!spuList.isEmpty()) {
            spuMsg.append("商品:");
            spuList.forEach(spec -> spuMsg.append("["+spec.getTitle()+"]"));
        }

        //如果分类被规格绑定 就不能被删除
        StringBuilder msg = new StringBuilder();
        Example groupExample = new Example(SpecGroupEntity.class);
        groupExample.createCriteria().andEqualTo("cid",id);
        List<SpecGroupEntity> specGroupList = specGroupMapper.selectByExample(groupExample);
        if (!specGroupList.isEmpty()) {
            msg.append("  规格:");
            specGroupList.forEach(spec -> msg.append("("+spec.getName()+")"));
        }


        //如果分类被品牌绑定 就不能被删除
        StringBuilder brandMsg = new StringBuilder();
        List<BrandEntity> brandList = brandMapper.getBrandByCategoryId(id);
        if (!brandList.isEmpty())  {
            brandMsg.append("  品牌:");
            brandList.forEach(b ->brandMsg.append("{"+b.getName()+"}"));
        }

        if (msg.length() > 0 || brandMsg.length() >0 || spuMsg.length() > 0)
            return this.setResultError(
                    categoryEntity.getName() + " 被" + spuMsg +" "+ msg +" "+ brandMsg +" 绑定,不能被删除");


        //没有子节点时,改变父节点状态
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId", categoryEntity.getParentId());
        List<CategoryEntity> list = categoryMapper.selectByExample(example);
        if (list.size() == 1){
            CategoryEntity parentEntity = new CategoryEntity();
            parentEntity.setId(categoryEntity.getParentId());
            parentEntity.setIsParent(0);
            categoryMapper.updateByPrimaryKeySelective(parentEntity);
        }

        categoryMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId) {

        List<CategoryEntity> byBrandId = categoryMapper.getByBrandId(brandId);
        return this.setResultSuccess(byBrandId);
    }

    //过滤查询
    @Override
    public Result<List<CategoryEntity>> getCateByIdList(String cidStr) {

        List<String> cidsStr = Arrays.asList(cidStr.split(","));

        List<Integer> cidList = cidsStr.stream().map(cid -> Integer.parseInt(cid)).collect(Collectors.toList());

        List<CategoryEntity> categoryList = categoryMapper.selectByIdList(cidList);

        return this.setResultSuccess(categoryList);
    }
}

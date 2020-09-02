package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.status.HTTPStatus;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

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
        if (null == id) {
            return this.setResultSuccess("id为空,删除失败");
        }
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        //判断是否是父节点
        if (categoryEntity.getIsParent() == 1){
            return this.setResultError("不能删除父节点");
        }
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
}

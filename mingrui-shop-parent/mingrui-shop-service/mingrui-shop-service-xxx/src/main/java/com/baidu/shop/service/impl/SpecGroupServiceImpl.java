package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecGroupService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecGroupServiceImpl
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/3
 * @Version V1.0
 **/
@RestController
@Transactional
public class SpecGroupServiceImpl extends BaseApiService implements SpecGroupService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    public Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO) {

        if(ObjectUtil.isNull(specGroupDTO.getCid())) return this.setResultSuccess();

        //通过分类id查询数据
        Example example = new Example(SpecGroupEntity.class);

        example.createCriteria().andEqualTo("cid",specGroupDTO.getCid());

        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }


    @Override
    public Result<JSONObject> save(SpecGroupDTO specGroupDTO) {
        specGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> update(SpecGroupDTO specGroupDTO) {
        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delete(Integer id) {

        //如果规格被参数绑定就不能被删除
        StringBuilder msg = new StringBuilder();

        SpecGroupEntity specGroupEntity = specGroupMapper.selectByPrimaryKey(id);

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId", id);
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);
        if(!list.isEmpty()) list.forEach(spe ->  msg.append("("+spe.getName()+")"));

        if (msg.length()>0) return this.setResultError(specGroupEntity.getName()+"被"+msg+"绑定,不能删除");

        specGroupMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }


    @Override
    public Result<List<SpecParamEntity>> list(SpecParamDTO specParamDTO) {

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",specParamDTO.getGroupId());
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> save(SpecParamDTO specParamDTO) {
        specParamMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> update(SpecParamDTO specParamDTO) {
        specParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> del(Integer id) {
        specParamMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }
}

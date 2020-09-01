package com.baidu.shop.base;

import com.baidu.shop.utils.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseDTO
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/31
 * @Version V1.0
 **/

@Data
@ApiModel(value="dto用于数据传输,分页/排序")
public class BaseDTO {

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer page;

    @ApiModelProperty(value = "每页显示多少条",example = "5")
    private Integer rows;

    @ApiModelProperty(value="排序字段")
    private String sort;

    @ApiModelProperty(value="是否降序")
    private Boolean order;

    //隐藏此方法,不让其swagger-ui上显示
    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){
        if(StringUtil.isNotEmpty(sort)){
            return sort+" "+(order?"desc":"");
        }
        return null;
    }
}

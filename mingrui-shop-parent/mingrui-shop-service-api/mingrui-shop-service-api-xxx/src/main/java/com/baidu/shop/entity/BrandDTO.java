package com.baidu.shop.entity;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BrandDTO
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Data
@ApiModel(value = "brandDTO数据传输")
public class BrandDTO extends BaseDTO {

    @ApiModelProperty(value = "品牌主键",example = "1")
    @NotNull(message = "id不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @NotEmpty(message = "name不能为空",groups = {MingruiOperation.Add.class})
    @ApiModelProperty(value = "品牌name")
    private String name;

    @ApiModelProperty(value = "品牌图片")
    private String image;

    @ApiModelProperty(value = "首字母")
    private Character letter;

    @ApiModelProperty(value = "品牌分类信息")
    @NotEmpty(message = "品牌分类信息不能为空",groups = {MingruiOperation.Add.class})
    private String category;
}

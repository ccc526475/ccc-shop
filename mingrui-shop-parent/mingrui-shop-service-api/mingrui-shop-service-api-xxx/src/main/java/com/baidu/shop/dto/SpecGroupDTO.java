package com.baidu.shop.dto;

import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName ScepGroupEntity
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/3
 * @Version V1.0
 **/
@ApiModel(value = "规格组数据传输")
@Data
public class SpecGroupDTO {

    @ApiModelProperty(value = "主键")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "商品id")
    @NotNull(message = "商品id不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid;

    @ApiModelProperty(value = "规格组名称")
    @NotEmpty(message = "规格组名称不能为空",groups = {MingruiOperation.Add.class})
    private String name;
}

package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.validate.group.MingruiOperation;
import com.sun.xml.internal.ws.api.model.MEP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SpuEntity
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/7
 * @Version V1.0
 **/
@Data
@ApiModel(value = "SPU数据传输")
public class SpuDTO extends BaseDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "标题")
    @NotEmpty(message = "标题不能为空",groups = {MingruiOperation.Add.class})
    private String title;

    @ApiModelProperty(value = "子标题")
    private String subTitle;

    @ApiModelProperty(value = "1级类目id")
    @NotNull(message = "1级类目不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid1;

    @ApiModelProperty(value = "2级类目id")
    @NotNull(message = "2级类目不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid2;

    @ApiModelProperty(value = "3级类目id")
    @NotNull(message = "3级类目不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid3;

    @ApiModelProperty(value = "商品所属品牌id")
    private Integer brandId;

    @ApiModelProperty(value = "是否上架,1上,0下")
    private Integer saleable;

    @ApiModelProperty(value = "是否有效,0无,1有")
    private Integer valid;

    private Date createTime;

    private Date lastUpdateTime;

    private String brandName;

    private String categoryName;

    @ApiModelProperty(value = "大字段数据")
    private SpuDetailDTO spuDetail;

    @ApiModelProperty(value = "sku属性集合")
    private List<SkuDTO>  skus;

}

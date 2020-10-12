package com.baidu.shop.BaseDTO;

import lombok.Data;

/**
 * @ClassName BaseSearch
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/9/27
 * @Version V1.0
 **/
@Data
public class BaseSearch {

    private String search;
    private Integer page;
    private String filterStr;

}

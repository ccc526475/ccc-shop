package com.atguigu.springcloud.entitis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CommonResult
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/24
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {

    private Integer code;

    private String message;

    private T      data;

    public CommonResult(Integer code,String message){
        this(code,message,null);
    }

}

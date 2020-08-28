package com.atguigu.springcloud.entitis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName Payment
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/24
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment{

    private Long id;

    private String serial;
}

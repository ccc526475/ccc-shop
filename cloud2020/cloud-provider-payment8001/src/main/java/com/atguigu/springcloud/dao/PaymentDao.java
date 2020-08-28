package com.atguigu.springcloud.dao;


import com.atguigu.springcloud.entitis.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentDao {

    //写操作   add/save
    public int create(Payment payment);

    //读操作
    public Payment getPaymentById(@Param("id") Long id);
}

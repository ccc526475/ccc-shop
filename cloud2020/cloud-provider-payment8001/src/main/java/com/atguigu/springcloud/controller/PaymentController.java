package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entitis.Payment;
import com.atguigu.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName PaymentController
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/25
 * @Version V1.0
 **/
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public Payment query(Long id){
        return paymentService.getPaymentById(id);
    }

}

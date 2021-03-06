package com.baidu.shop.status;

/**
 * @ClassName HTTPStatus
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/27
 * @Version V1.0
 **/
public class HTTPStatus {

    public static final int OK = 200;//成功

    public static final int ERROR = 500;//失败

    public static final int OPERATION_ERROR = 5001;//操作失败

    public static final int PARAMS_VALIDATE_ERROR = 5002;//参数校验失败

    public static final int USER_USER_PASSWORD_ERROR = 5004; //账号密码错误

    public static final int TOKEN_ERROR = 403;  //TOKEN  失效失效

}

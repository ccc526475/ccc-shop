package com.baidu.filter;
import com.baidu.config.JwtConfig;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName LoginFilter
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/10/17
 * @Version V1.0
 **/
@Component
public class LoginFilter extends ZuulFilter{

    @Autowired
    private JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();
        //获取请求的url
        String requestURI = request.getRequestURI();
        //当前请求如过不在白名单,则开启拦截
        logger.info("~~~~~~~~~~"+requestURI);
        //如果当前请求时登录请求,不执行拦截
        boolean b = !jwtConfig.getExcludePath().contains(requestURI);
        return b;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();
        logger.info("拦截到请求"+request.getRequestURI());
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        logger.info("token信息:"+token);
        //校验
        if (ObjectUtil.isNotNull(token)){
            try {
                //通过公钥解密,如果成功就放行,失败就拦截
                JwtUtils.getInfoFromToken(token,jwtConfig.getPublicKey());
            } catch (Exception e) {
                logger.debug("校验异常 拦截"+token);
                //返回403
                currentContext.setSendZuulResponse(false);
                currentContext.setResponseStatusCode(HTTPStatus.TOKEN_ERROR);
            }
        }else{
            logger.debug("校验异常 拦截"+token);
            //返回403
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HTTPStatus.TOKEN_ERROR);
        }
        return null;
    }
}

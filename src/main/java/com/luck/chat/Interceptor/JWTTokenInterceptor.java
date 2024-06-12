package com.luck.chat.Interceptor;


import com.luck.chat.constant.JWTClaimsConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.properties.JWTProperties;
import com.luck.chat.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt校验令牌拦截器
 */
@Component
@Slf4j
public class JWTTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTProperties jwtProperties;


    /**
     * 拦截器校验JWT
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception{
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，返回
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Jws<Claims> claimsJws = JWTUtils.parseJWT(token,jwtProperties.getSecretKey());
            Long userId = Long.valueOf(claimsJws.getPayload().get(JWTClaimsConstant.USER_ID).toString());
            //存放当前用户id
            BaseContext.setCurrentId(userId);
            log.info("当前用户id：{}", userId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }

    }

    /**
     * 移除threadLocal资源
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //释放线程资源改为在websocket连接断开时释放
//        BaseContext.removeCurrentId();
    }
}


package com.luck.chat.Interceptor;

import com.luck.chat.constant.JWTClaimsConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.properties.JWTProperties;
import com.luck.chat.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * websocket握手拦截
 */
@Slf4j
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Autowired
    private JWTProperties jwtProperties;

    /**
     * 握手拦截器
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("握手开始前");
//        HttpHeaders headers=request.getHeaders();
//        List<String> header=headers.get(jwtProperties.getTokenName());
//        if(header==null|| header.isEmpty()){
//            response.setStatusCode(HttpStatusCode.valueOf(401));
//            return false;
//        }
//
//        //1、从请求头中获取令牌
//        String token= header.get(0);
//        if(StringUtils.isBlank(token)){
//            response.setStatusCode(HttpStatusCode.valueOf(401));
//            return false;
//        }
//        //2、校验令牌
//        try {
//            log.info("jwt校验:{}", token);
//            Jws<Claims> claimsJws = JWTUtils.parseJWT(token,jwtProperties.getSecretKey());
//            Long userId = Long.valueOf(claimsJws.getPayload().get(JWTClaimsConstant.USER_ID).toString());
//            //存放当前用户id
//            BaseContext.setCurrentId(userId);
//            log.info("当前用户id：{}", userId);
//            //3、通过，放行
//            return true;
//        } catch (Exception ex) {
//            //4、不通过，响应401状态码
//            response.setStatusCode(HttpStatusCode.valueOf(401));
//            return false;
//        }
        return true;
    }

    /**
     * 握手结束之后
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
//        log.info("握手结束");
    }
}

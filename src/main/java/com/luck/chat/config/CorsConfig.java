package com.luck.chat.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class CorsConfig {
    @Bean
    public OncePerRequestFilter corsFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                //允许所有来源
                String allowOrigin = "*";
                //允许以下请求方法
                String allowMethods = "GET,POST,PUT,DELETE,OPTIONS";
                //允许以下请求头
                String allowHeaders = "Content-Type,X-Token,Authorization";
                //允许有认证信息（cookie）
                String allowCredentials = "true";

//                String origin = request.getHeader("Origin");
                //此处是为了兼容需要认证信息(cookie)的时候不能设置为 * 的问题
                response.setHeader("Access-Control-Allow-Origin", allowOrigin);
                response.setHeader("Access-Control-Allow-Methods", allowMethods);
//                response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
                response.setHeader("Access-Control-Allow-Headers", allowHeaders);

                //处理 OPTIONS 的请求
                if ("OPTIONS".equals(request.getMethod())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}

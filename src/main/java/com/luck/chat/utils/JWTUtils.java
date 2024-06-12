package com.luck.chat.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;


public class JWTUtils {

    /**
     * 生成jwt令牌
     * @param secretKey
     * @param expiration
     * @param clams
     * @return
     */
    public static String generateJWT(String secretKey, long expiration, Map<String,Object> clams){
        //指定加密算法
        SecureDigestAlgorithm<SecretKey, SecretKey> algorithm = Jwts.SIG.HS256;
        //生成JWT的时间
        long expMillis = System.currentTimeMillis()+expiration;
        Date exp = new Date(expMillis);
        //密钥实例
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        String jwt=Jwts.builder()
                .signWith(key,algorithm)
                .expiration(exp)
                .claims(clams)
                .compact();

        return jwt;
    }

    /**
     * 解析jwt
     * @param token
     * @param secretKey
     * @return
     */
    public static Jws<Claims> parseJWT(String token, String secretKey){
        //密钥实例
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)  //设置签名的密钥
                .build()
                .parseSignedClaims(token); //设置要解析的jwt

        return claimsJws;
    }





}

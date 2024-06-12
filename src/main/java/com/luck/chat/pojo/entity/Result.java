package com.luck.chat.pojo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0失败
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }
    public static <T> Result<T> error(){
        Result result=new Result();
        result.code=0;
        return result;
    }

    public static <T> Result<T> error(Integer code) {
        Result result = new Result();
        result.code = code;
        return result;
    }
}

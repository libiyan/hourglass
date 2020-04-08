package com.drgou.exception;

/**
 * 自定义REST接口请求异常类
 * @author CHH
 * @create 2017-11-24
 **/
public class RestRequestException extends RuntimeException {

    public RestRequestException() {
        super("请求发生异常！");
    }

    public RestRequestException(String message) {
        super(message);
    }
}

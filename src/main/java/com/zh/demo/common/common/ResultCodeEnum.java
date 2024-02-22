package com.zh.demo.common.common;


public enum ResultCodeEnum {

    // 10??? 通用
    SUCCESS(10000,"请求成功"),
    FAILED(10008, "请求失败"),
    FAILED_PARAM_ERROR(10009, "参数错误");

    // 11???

    private Integer code;

    private String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

}

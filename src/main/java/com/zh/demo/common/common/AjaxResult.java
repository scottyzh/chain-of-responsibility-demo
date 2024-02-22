package com.zh.demo.common.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("API通用返回数据")
@Data
public class AjaxResult<T> {

    @ApiModelProperty(value = "状态码", example = "200")
    private final int code;

    @ApiModelProperty(value = "返回消息", example = "success")
    private final String message;

    @ApiModelProperty("数据对象")
    private final T data;

    public AjaxResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static <T> AjaxResult<T> success() {
        return new AjaxResult<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), null);
    }

    public static <T> AjaxResult<T> success(String message) {
        return new AjaxResult<>(ResultCodeEnum.SUCCESS.getCode(), message, null);
    }

    public static <T> AjaxResult<T> success(T data) {
        return new AjaxResult<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> AjaxResult<T> success(String message, T data) {
        return new AjaxResult<>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> AjaxResult<T> failed() {
        return new AjaxResult<>(ResultCodeEnum.FAILED.getCode(), ResultCodeEnum.FAILED.getMessage(), null);
    }

    public static <T> AjaxResult<T> failed(String message) {
        return new AjaxResult<>(ResultCodeEnum.FAILED.getCode(), message, null);
    }

    public static <T> AjaxResult<T> failed(ResultCodeEnum resultCodeEnum) {
        return new AjaxResult<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }

    public static <T> AjaxResult<T> failed(String message, T data) {
        return new AjaxResult<>(ResultCodeEnum.FAILED.getCode(), message, data);
    }

    public static <T> AjaxResult<T> failed(ResultCodeEnum resultCodeEnum, T data) {
        return new AjaxResult<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), data);
    }
}




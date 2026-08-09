package com.bkbits.core;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应结果，统一 API 返回结构。
 *
 * @param <T> 业务数据类型
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 默认成功状态码 */
    public static final int CODE_SUCCESS = 200;

    /** 默认失败状态码 */
    public static final int CODE_FAILURE = 500;

    /** 是否成功 */
    private boolean ok;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 业务数据 */
    private T data;

    /** 无参构造（JSON 反序列化使用） */
    public Result() {
    }

    /**
     * 构造成功结果（无数据）。
     *
     * @return 成功结果，code=200，message=OK
     */
    public static <T> Result<T> ok() {
        return ok(null, "OK");
    }

    /**
     * 构造成功结果（带数据）。
     *
     * @param data 业务数据
     * @return 成功结果，code=200，message=OK
     */
    public static <T> Result<T> ok(T data) {
        return ok(data, "OK");
    }

    /**
     * 构造成功结果（带数据与提示）。
     *
     * @param data    业务数据
     * @param message 提示信息
     * @return 成功结果，code=200
     */
    public static <T> Result<T> ok(T data, String message) {
        return ok(data, message, CODE_SUCCESS);
    }

    /**
     * 构造成功结果（带数据、提示与状态码）。
     *
     * @param data    业务数据
     * @param message 提示信息
     * @param code    状态码
     * @return 成功结果
     */
    public static <T> Result<T> ok(T data, String message, int code) {
        Result<T> result = new Result<>();
        result.setOk(true);
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 构造失败结果（默认状态码 500）。
     *
     * @return 失败结果，code=500，message=操作失败
     */
    public static <T> Result<T> fail() {
        return fail("操作失败");
    }

    /**
     * 构造失败结果（带提示，默认状态码 500）。
     *
     * @param message 提示信息
     * @return 失败结果，code=500
     */
    public static <T> Result<T> fail(String message) {
        return fail(CODE_FAILURE, message);
    }

    /**
     * 构造失败结果（带状态码与提示）。
     *
     * @param code    状态码
     * @param message 提示信息
     * @return 失败结果
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.setOk(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}

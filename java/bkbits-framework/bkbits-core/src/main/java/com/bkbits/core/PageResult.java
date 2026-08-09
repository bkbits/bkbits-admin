package com.bkbits.core;

import java.util.List;

/**
 * 分页响应结果。
 *
 * @param <T> 行数据类型
 */
public class PageResult<T> extends Result<PageData<T>> {

    /**
     * 构造成功分页结果。
     *
     * @param total 总记录数
     * @param rows  当前页数据
     * @return 成功分页结果，code=200，data 包含分页信息
     */
    public static <T> PageResult<T> page(long total, List<T> rows) {
        PageResult<T> result = new PageResult<>();
        result.setOk(true);
        result.setCode(CODE_SUCCESS);
        result.setMessage("OK");
        result.setData(new PageData<>(total, rows));
        return result;
    }

    /**
     * 构造失败结果（默认状态码 500）。
     *
     * @return 失败分页结果，code=500，message=操作失败
     */
    public static <T> PageResult<T> pageFail() {
        return pageFail("操作失败");
    }

    /**
     * 构造失败结果（带提示，默认状态码 500）。
     *
     * @param message 提示信息
     * @return 失败分页结果，code=500
     */
    public static <T> PageResult<T> pageFail(String message) {
        return pageFail(CODE_FAILURE, message);
    }

    /**
     * 构造失败结果（带状态码与提示）。
     *
     * @param code    状态码
     * @param message 提示信息
     * @return 失败分页结果
     */
    public static <T> PageResult<T> pageFail(int code, String message) {
        PageResult<T> result = new PageResult<>();
        result.setOk(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}

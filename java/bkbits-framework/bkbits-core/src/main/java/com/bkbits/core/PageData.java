package com.bkbits.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页数据，承载分页查询结果。
 *
 * @param <T> 行数据类型
 */
@Data
@AllArgsConstructor
public class PageData<T> {

    /** 总记录数 */
    private long total;

    /** 当前页数据 */
    private List<T> rows;
}

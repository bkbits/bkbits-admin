package com.bkbits.orm;

import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.api.pagination.Pager;
import com.easy.query.core.basic.api.select.executor.PageAble;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EasyPager<T> implements Pager<T, PageResult<T>> {
    private PageQuery pageQuery;

    public static <T> EasyPager<T> current() {
        return new EasyPager<>(PageQuery.current());
    }

    public static <T> EasyPager<T> of(long page, long pageSize) {
        return new EasyPager<>(PageQuery.of(page, pageSize));
    }

    @Override
    public PageResult<T> toResult(PageAble<T> query) {
        EasyPageResult<T> result = query.toPageResult(pageQuery.getPage(), pageQuery.getPageSize());
        return PageResult.of(result);
    }
}

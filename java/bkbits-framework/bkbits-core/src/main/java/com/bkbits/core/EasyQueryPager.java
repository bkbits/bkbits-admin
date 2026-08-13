package com.bkbits.core;

import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.api.pagination.Pager;
import com.easy.query.core.basic.api.select.executor.PageAble;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class EasyQueryPager<T> implements Pager<T, PageResult<T>> {
    private PageQuery pageQuery;

    @Override
    public PageResult<T> toResult(PageAble<T> query) {
        EasyPageResult<T> result = query.toPageResult(pageQuery.getPage(), pageQuery.getPageSize());
        return PageResult.of(result);
    }
}

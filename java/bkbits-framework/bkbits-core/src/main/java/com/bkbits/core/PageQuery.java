package com.bkbits.core;

import com.bkbits.util.JsonUtil;
import com.easy.query.core.api.pagination.Pager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {
    private long page = 1;
    private long pageSize = 10;

    /**
     * 构造分页查询参数
     *
     * @param page     页码，必须 >= 1
     * @param pageSize 分页尺寸，必须 >= 1
     * @return 分页查询参数
     */
    public static PageQuery of(long page, long pageSize) {
        if (page < 1) {
            throw new IllegalArgumentException("page参数必须 >= 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize参数必须 >= 1");
        }
        return new PageQuery(page, pageSize);
    }

    /**
     * 自动从当前上下文中获取
     *
     * @return 分页查询参数
     */
    public static PageQuery current() {
        Context context = Context.current();
        String pageStr = context.param("page");
        String pageSizeStr = context.param("pageSize");

        Long page = null;
        Long pageSize = null;

        try {
            if (pageStr != null) {
                page = Long.parseLong(pageStr);
            }
        } catch (NumberFormatException e) {
            log.error("page 解析错误", e);
        }

        try {
            pageSize = Long.parseLong(pageSizeStr);
        } catch (NumberFormatException e) {
            log.error("pageSize 解析错误", e);
        }


        if (page == null && pageSize == null) {
            try {
                String body = context.body();
                PageQuery pageQuery = JsonUtil.parse(body, PageQuery.class);
                page = pageQuery.getPage();
                pageSize = pageQuery.getPageSize();
            } catch (IOException e) {
                log.trace("解析body PageQuery错误", e);
            }
        }

        if (page == null) {
            page = 1L;
        }

        if (pageSize == null) {
            pageSize = 10L;
        }

        if (page < 1) {
            throw new StatusException("page参数不正确: page = " + page, 400);
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new StatusException("pageSize参数不正确: pageSize = " + pageSize, 400);
        }

        return new PageQuery(
                page,
                pageSize
        );
    }

    /**
     * 转为easyquery分页器
     *
     * @param <T> 类型
     * @return 分页器
     */
    public <T> Pager<T, PageResult<T>> toPager() {
        return new EasyQueryPager<>(this);
    }

    /**
     * 转为easyquery分页器
     *
     * @param clazz 类型
     * @param <T>   类型
     * @return 分页器
     */
    public <T> Pager<T, PageResult<T>> toPager(Class<T> clazz) {
        return new EasyQueryPager<>(this);
    }
}

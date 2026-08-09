package com.bkbits.core;

import com.bkbits.utils.JsonUtil;
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

    public static PageQuery of() {
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

        if (page <= 0) {
            throw new StatusException("page参数不正确: page = " + page, 400);
        }
        if (pageSize <= 1 || pageSize > 100) {
            throw new StatusException("pageSize参数不正确: pageSize = " + pageSize, 400);
        }

        return new PageQuery(
                page,
                pageSize
        );
    }
}

package com.bkbits.monitor;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

@Slf4j
@Component
public class ApiMonitorFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        String url = ctx.url();
        final long nanoTime = System.nanoTime();
        try {
            chain.doFilter(ctx);
        } finally {
            long costTime = System.nanoTime() - nanoTime;
            log.info("api monitor: url={}, cost={}ms", costTime, nanoTime / 1_000_000);
        }
    }
}

package com.bkbits.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 线程池异步工具类。
 *
 * <p>基于 JDK 21 虚拟线程：普通任务由虚拟线程执行器执行（适合 IO 密集型任务），
 * 延迟/周期任务由定时线程池调度。应用退出时自动优雅关闭。</p>
 */
public final class AsyncUtil {

    /** 虚拟线程执行器（普通异步任务） */
    private static final ExecutorService EXECUTOR = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("bkbits-async-", 0).factory());

    /** 定时线程池（延迟/周期任务） */
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "bkbits-scheduler");
            t.setDaemon(true);
            return t;
        }
    });

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(AsyncUtil::shutdown, "bkbits-async-shutdown"));
    }

    private AsyncUtil() {
    }

    /** 异步执行任务（不关心结果） */
    public static void execute(Runnable task) {
        EXECUTOR.execute(task);
    }

    /** 异步提交任务，返回 Future */
    public static Future<?> submit(Runnable task) {
        return EXECUTOR.submit(task);
    }

    /** 异步提交带返回值任务，返回 Future */
    public static <T> Future<T> submit(Callable<T> task) {
        return EXECUTOR.submit(task);
    }

    /** 延迟执行任务 */
    public static ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return SCHEDULER.schedule(task, delay, unit);
    }

    /** 固定频率周期执行（按固定间隔调度，不等待任务完成） */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return SCHEDULER.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /** 固定延迟周期执行（等待任务完成后延迟指定时间再执行） */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        return SCHEDULER.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    /** 是否已关闭 */
    public static boolean isShutdown() {
        return EXECUTOR.isShutdown() && SCHEDULER.isShutdown();
    }

    /** 优雅关闭线程池 */
    public static void shutdown() {
        EXECUTOR.shutdown();
        SCHEDULER.shutdown();
    }
}

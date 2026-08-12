package com.bkbits.util;

import lombok.experimental.UtilityClass;

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
@UtilityClass
public class AsyncUtil {

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

    /**
     * 异步执行任务（不关心结果）。
     *
     * @param task 待执行任务，不能为 null
     */
    public static void execute(Runnable task) {
        EXECUTOR.execute(task);
    }

    /**
     * 异步提交任务，返回 Future。
     *
     * @param task 待执行任务，不能为 null
     * @return 任务执行句柄 {@link Future}
     */
    public static Future<?> submit(Runnable task) {
        return EXECUTOR.submit(task);
    }

    /**
     * 异步提交带返回值任务，返回 Future。
     *
     * @param task 待执行任务，不能为 null
     * @param <T>  返回值类型
     * @return 任务执行句柄 {@link Future}
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return EXECUTOR.submit(task);
    }

    /**
     * 延迟执行任务。
     *
     * @param task  待执行任务，不能为 null
     * @param delay 延迟时长
     * @param unit  时长单位
     * @return 调度句柄 {@link ScheduledFuture}
     */
    public static ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return SCHEDULER.schedule(task, delay, unit);
    }

    /**
     * 固定频率周期执行（按固定间隔调度，不等待任务完成）。
     *
     * @param task         待执行任务，不能为 null
     * @param initialDelay 首次执行延迟
     * @param period       执行间隔
     * @param unit         时长单位
     * @return 调度句柄 {@link ScheduledFuture}
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return SCHEDULER.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * 固定延迟周期执行（等待任务完成后延迟指定时间再执行）。
     *
     * @param task         待执行任务，不能为 null
     * @param initialDelay 首次执行延迟
     * @param delay        任务完成后的固定延迟
     * @param unit         时长单位
     * @return 调度句柄 {@link ScheduledFuture}
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        return SCHEDULER.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    /**
     * 是否已关闭。
     *
     * @return 两个线程池均已关闭时返回 {@code true}
     */
    public static boolean isShutdown() {
        return EXECUTOR.isShutdown() && SCHEDULER.isShutdown();
    }

    /**
     * 优雅关闭线程池（等待已提交任务执行完毕）。
     */
    public static void shutdown() {
        EXECUTOR.shutdown();
        SCHEDULER.shutdown();
    }
}

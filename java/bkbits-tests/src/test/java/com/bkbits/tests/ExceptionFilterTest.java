package com.bkbits.tests;

import com.bkbits.core.Result;
import com.bkbits.exception.ExceptionAdvice;
import com.bkbits.exception.ExceptionFilter;
import com.bkbits.exception.annotations.ExceptionHandler;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.FilterChain;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExceptionFilterTest {
    @Test
    public void passesMatchedCauseToAdvice() throws Throwable {
        RecordingAdvice advice = new RecordingAdvice();
        ExceptionFilter filter = new ExceptionFilter(List.of(advice));
        ContextEmpty ctx = new ContextEmpty();
        IllegalArgumentException inner = new IllegalArgumentException("invalid argument");

        filter.doFilter(ctx, throwing(new RuntimeException("wrapper", inner)));

        assertSame(inner, advice.received);
        assertEquals(400, ctx.status());
    }

    @Test
    public void rethrowsNestedErrorBeforeInvokingOuterHandler() {
        RecordingAdvice advice = new RecordingAdvice();
        ExceptionFilter filter = new ExceptionFilter(List.of(advice));
        AssertionError fatal = new AssertionError("fatal");
        IllegalArgumentException outer = new IllegalArgumentException("wrapper", fatal);

        AssertionError thrown = assertThrows(AssertionError.class,
                () -> filter.doFilter(new ContextEmpty(), throwing(outer)));

        assertSame(fatal, thrown);
        assertNull(advice.received);
    }

    @Test
    public void interruptedAdviceLeavesRenderedResponseUntouched() throws Throwable {
        ContextEmpty ctx = new ContextEmpty();
        ctx.status(204);

        assertAdviceRestoresInterrupt(ctx, () -> ctx.setRendered(true));

        assertEquals(204, ctx.status());
        assertFalse(ctx.isHeadersSent());
    }

    @Test
    public void interruptedAdviceLeavesSentResponseUntouched() throws Throwable {
        ContextEmpty ctx = new ContextEmpty();
        ctx.status(202);

        assertAdviceRestoresInterrupt(ctx, () -> ctx.output("accepted"));

        assertEquals(202, ctx.status());
        assertEquals("accepted", responseBody(ctx));
    }

    @Test
    public void rendersWrappedStatusExceptionAsPlainText() throws Throwable {
        ExceptionFilter filter = new ExceptionFilter(List.of());
        ContextEmpty ctx = new ContextEmpty();
        StatusException status = new StatusException("method not allowed", 405);

        filter.doFilter(ctx, throwing(new RuntimeException("wrapper", status)));

        assertEquals(405, ctx.status());
        assertEquals("method not allowed", responseBody(ctx));
    }

    private static void assertAdviceRestoresInterrupt(ContextEmpty ctx, Runnable startResponse) throws Throwable {
        ExceptionFilter filter = new ExceptionFilter(List.of(new InterruptingAdvice(startResponse)));
        assertFalse(Thread.currentThread().isInterrupted());
        try {
            filter.doFilter(ctx, throwing(new IllegalStateException()));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    private static FilterChain throwing(Throwable failure) {
        return ctx -> { throw failure; };
    }

    private static String responseBody(ContextEmpty ctx) {
        return ((ByteArrayOutputStream) ctx.outputStream()).toString(StandardCharsets.UTF_8);
    }

    public static class RecordingAdvice implements ExceptionAdvice {
        private RuntimeException received;

        @ExceptionHandler(IllegalArgumentException.class)
        public Result<?> handle(RuntimeException failure) {
            received = failure;
            return Result.fail(400, failure.getMessage());
        }
    }

    public static class InterruptingAdvice implements ExceptionAdvice {
        private final Runnable startResponse;

        InterruptingAdvice(Runnable startResponse) {
            this.startResponse = startResponse;
        }

        @ExceptionHandler(IllegalStateException.class)
        public Result<?> handle() throws InterruptedException {
            startResponse.run();
            throw new InterruptedException();
        }
    }
}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import com.yegor256.TogetherFailure;
import com.yegor256.together.failure.FailureContext;
import com.yegor256.together.failure.FailureKind;
import com.yegor256.together.failure.RaisedFailure;
import com.yegor256.together.policy.Deadline;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Started round execution.
 *
 * @param <T> Type of result
 * @since 1.0
 */
public final class Started<T> {

    /**
     * Shared latch.
     */
    private final CountDownLatch latch;

    /**
     * Completion service.
     */
    private final CompletionService<Execution<T>> completion;

    /**
     * All futures.
     */
    private final Map<Integer, Future<Execution<T>>> futures;

    /**
     * Start time.
     */
    private long begun;

    /**
     * Ctor.
     * @param shared Shared latch
     * @param service Completion service
     * @param all Futures
     */
    public Started(final CountDownLatch shared,
        final CompletionService<Execution<T>> service,
        final Map<Integer, Future<Execution<T>>> all) {
        this.latch = shared;
        this.completion = service;
        this.futures = new HashMap<>(all);
    }

    /**
     * Start all jobs.
     */
    public void start() {
        this.begun = System.nanoTime();
        this.latch.countDown();
    }

    /**
     * Wait forever for next execution.
     * @param round Round number
     * @return Execution
     */
    public Execution<T> next(final int round) {
        final Future<Execution<T>> future;
        try {
            future = this.completion.take();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            this.stop();
            throw Started.failure(
                new FailureContext(round, -1, this.elapsed()), false, ex
            );
        }
        return this.execution(round, future);
    }

    /**
     * Wait with timeout.
     * @param round Round number
     * @param deadline Timeout deadline
     * @param pending Missing thread
     * @return Execution
     */
    public Execution<T> next(final int round, final Deadline deadline,
        final int pending) {
        final Future<Execution<T>> future = this.next(deadline, round);
        if (future == null) {
            this.stop();
            throw Started.failure(
                new FailureContext(round, pending, this.elapsed()),
                true,
                new TimeoutException("Timeout was exceeded")
            );
        }
        return this.execution(round, future);
    }

    /**
     * Stop all jobs.
     */
    public void stop() {
        for (final Future<Execution<T>> future : this.futures.values()) {
            future.cancel(true);
        }
    }

    /**
     * Elapsed time in milliseconds.
     * @return Milliseconds
     */
    public long elapsed() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.begun);
    }

    /**
     * Wait with timeout.
     * @param deadline Timeout deadline
     * @param round Round number
     * @return Future or NULL
     */
    private Future<Execution<T>> next(final Deadline deadline, final int round) {
        final Future<Execution<T>> future;
        try {
            future = this.completion.poll(
                deadline.from(this.begun),
                deadline.timeUnit()
            );
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            this.stop();
            throw Started.failure(
                new FailureContext(round, -1, this.elapsed()), false, ex
            );
        }
        return future;
    }

    /**
     * Read execution from future.
     * @param round Round number
     * @param future Future
     * @return Execution
     */
    private Execution<T> execution(final int round,
        final Future<Execution<T>> future) {
        try {
            return future.get();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            this.stop();
            throw Started.failure(
                new FailureContext(round, this.threadOf(future), this.elapsed()),
                false,
                ex
            );
        } catch (final ExecutionException ex) {
            this.stop();
            throw Started.failure(
                new FailureContext(round, this.threadOf(future), this.elapsed()),
                false,
                Started.reasonOf(ex)
            );
        } catch (final CancellationException ex) {
            this.stop();
            throw Started.failure(
                new FailureContext(round, this.threadOf(future), this.elapsed()),
                false,
                ex
            );
        }
    }

    /**
     * Thread number of the future.
     * @param future Future to find
     * @return Thread number
     */
    private int threadOf(final Future<Execution<T>> future) {
        int thread = -1;
        for (final Map.Entry<Integer, Future<Execution<T>>> entry
            : this.futures.entrySet()) {
            if (entry.getValue().equals(future)) {
                thread = entry.getKey();
                break;
            }
        }
        return thread;
    }

    /**
     * Root cause of execution failure.
     * @param exception Failure
     * @return Cause
     */
    private static Throwable reasonOf(final ExecutionException exception) {
        Throwable reason = exception;
        if (exception.getCause() != null) {
            reason = exception.getCause();
        }
        return reason;
    }

    /**
     * Create public failure.
     * @param context Failure context
     * @param timeout Whether timeout happened
     * @param cause Root cause
     * @return Failure
     */
    private static TogetherFailure failure(final FailureContext context,
        final boolean timeout, final Throwable cause) {
        return new RaisedFailure(context, new FailureKind(timeout, cause.getMessage()))
            .asPublic(cause);
    }
}

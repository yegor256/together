/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import com.yegor256.Together;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * One concurrent job.
 * @param <T> Type of result
 * @since 1.0
 */
public final class Job<T> implements Callable<Execution<T>> {

    /**
     * Start latch.
     */
    private final CountDownLatch latch;

    /**
     * Thread number.
     */
    private final int thread;

    /**
     * Action.
     */
    private final Together.Action<T> action;

    /**
     * Ctor.
     * @param shared Shared latch
     * @param number Thread number
     * @param job Action
     */
    public Job(final CountDownLatch shared, final int number,
        final Together.Action<T> job) {
        this.latch = shared;
        this.thread = number;
        this.action = job;
    }

    @Override
    public Execution<T> call() throws Exception {
        this.latch.await();
        return new Execution<>(
            this.thread,
            this.action.apply(this.thread),
            Job.elapsed(System.nanoTime())
        );
    }

    /**
     * Elapsed time in milliseconds.
     * @param start Start time
     * @return Milliseconds
     */
    private static long elapsed(final long start) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }
}

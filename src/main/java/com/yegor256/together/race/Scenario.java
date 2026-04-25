/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import com.yegor256.Together;
import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Execution;
import com.yegor256.together.execution.Job;
import com.yegor256.together.execution.Started;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Race scenario.
 * @param <T> Type of result
 * @since 1.0
 */
public final class Scenario<T> {

    /**
     * Threads.
     */
    private final Threads threads;

    /**
     * Action.
     */
    private final Together.Action<T> action;

    /**
     * Ctor.
     * @param total Threads
     * @param job Action
     */
    public Scenario(final Threads total, final Together.Action<T> job) {
        // @checkstyle ConstructorsCodeFreeCheck (1 line)
        this.threads = total.copy();
        this.action = job;
    }

    /**
     * New service.
     * @return Executor service
     */
    public ExecutorService newService() {
        return this.threads.newService();
    }

    /**
     * Submit all jobs.
     * @param service Executor service
     * @return Started execution
     */
    public Started<T> startedOn(final ExecutorService service) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Map<Integer, Future<Execution<T>>> futures = new HashMap<>();
        final ExecutorCompletionService<Execution<T>> completion =
            new ExecutorCompletionService<>(service);
        for (final int thread : this.threads.inRandomOrder()) {
            futures.put(
                thread,
                completion.submit(new Job<>(latch, thread, this.action))
            );
        }
        return new Started<>(latch, completion, futures);
    }

    /**
     * New completion collector.
     * @return Collector
     */
    public Completed<T> newCompleted() {
        return new Completed<>(this.threads);
    }
}

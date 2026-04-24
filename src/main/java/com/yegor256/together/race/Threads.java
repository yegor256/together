/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import com.yegor256.together.execution.Execution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread numbers.
 *
 * @since 1.0
 */
public final class Threads {

    /**
     * Total threads.
     */
    private final int total;

    /**
     * Ctor.
     * @param count Number of threads
     */
    public Threads(final int count) {
        this.total = count;
    }

    /**
     * Create executor service.
     * @return Service
     */
    public ExecutorService newService() {
        return Executors.newFixedThreadPool(this.total);
    }

    /**
     * Copy itself.
     * @return Copy
     */
    public Threads copy() {
        return new com.yegor256.together.race.Threads(this.total);
    }

    /**
     * Thread numbers in random order.
     * @return Numbers
     */
    public Iterable<Integer> inRandomOrder() {
        final List<Integer> numbers = new ArrayList<>(this.total);
        for (int idx = 0; idx < this.total; ++idx) {
            numbers.add(idx);
        }
        Collections.shuffle(numbers);
        return numbers;
    }

    /**
     * Whether some threads are still missing.
     * @param done Completed executions
     * @return TRUE if some are missing
     */
    public boolean misses(final Map<Integer, Execution<?>> done) {
        return done.size() < this.total;
    }

    /**
     * Find first absent thread.
     * @param done Completed executions
     * @return Missing thread
     */
    public int firstAbsentFrom(final Map<Integer, Execution<?>> done) {
        int thread = -1;
        for (int idx = 0; idx < this.total; ++idx) {
            if (!done.containsKey(idx)) {
                thread = idx;
                break;
            }
        }
        return thread;
    }

    /**
     * Add all results in order.
     * @param done Completed executions
     * @param round Round number
     * @param results Mutable results
     * @param <T> Type of result
     */
    public <T> void appendTo(final Map<Integer, Execution<T>> done,
        final int round, final List<T> results) {
        for (int idx = 0; idx < this.total; ++idx) {
            done.get(idx).complete(round, results);
        }
    }
}

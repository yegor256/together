/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import com.yegor256.together.race.Threads;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Completed executions.
 *
 * @param <T> Type of result
 * @since 1.0
 */
public final class Completed<T> {

    /**
     * Thread layout.
     */
    private final Threads threads;

    /**
     * Completed executions.
     */
    private final Map<Integer, Execution<T>> done;

    /**
     * Ctor.
     * @param all Threads
     */
    public Completed(final Threads all) {
        this(all, new HashMap<>());
    }

    /**
     * Private ctor.
     * @param all Threads
     * @param map Completed executions
     */
    private Completed(final Threads all, final Map<Integer, Execution<T>> map) {
        this.threads = all;
        this.done = map;
    }

    /**
     * Add one execution.
     * @param execution Next execution
     * @return This collector
     */
    public Completed<T> with(final Execution<T> execution) {
        execution.placeInto(this.done);
        return this;
    }

    /**
     * Whether more executions are expected.
     * @return TRUE if yes
     */
    public boolean isIncomplete() {
        return this.threads.misses(new HashMap<Integer, Execution<?>>(this.done));
    }

    /**
     * Missing thread number.
     * @return Thread number
     */
    public int missingThread() {
        return this.threads.firstAbsentFrom(new HashMap<Integer, Execution<?>>(this.done));
    }

    /**
     * Stop if any failure already happened.
     * @param round Round number
     * @param started Started execution
     */
    public void stopFastIn(final int round, final Started<T> started) {
        for (final Execution<T> execution : this.done.values()) {
            execution.stopFastIn(round, started);
        }
    }

    /**
     * Final results.
     * @param round Round number
     * @return Results
     */
    public List<T> resultsIn(final int round) {
        final List<T> results = new LinkedList<>();
        this.threads.appendTo(this.done, round, results);
        return results;
    }
}

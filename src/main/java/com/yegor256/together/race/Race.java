/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import com.yegor256.Together;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Concurrent race.
 *
 * @param <T> Type of result
 * @since 1.0
 */
public final class Race<T> implements Iterable<T> {

    /**
     * Default amount of threads.
     */
    private static final int DEFAULT =
        Math.max(Runtime.getRuntime().availableProcessors(), 3);

    /**
     * Scenario.
     */
    private final Scenario<T> scenario;

    /**
     * Rules.
     */
    private final Rules rules;

    /**
     * Ctor.
     * @param action Action
     */
    public Race(final Together.Action<T> action) {
        this(Race.DEFAULT, action);
    }

    /**
     * Ctor.
     * @param total Threads
     * @param action Action
     */
    public Race(final int total, final Together.Action<T> action) {
        this(new Scenario<>(new Threads(total), action), new Rules());
    }

    /**
     * Private ctor.
     * @param origin Scenario
     * @param law Rules
     */
    private Race(final Scenario<T> origin, final Rules law) {
        this.scenario = origin;
        this.rules = law;
    }

    /**
     * Repeat it.
     * @param total Rounds
     * @return New race
     */
    public Race<T> repeated(final int total) {
        return new com.yegor256.together.race.Race<>(
            this.scenario, this.rules.repeated(total)
        );
    }

    /**
     * Limit its duration.
     * @param limit Timeout
     * @param unit Unit
     * @return New race
     */
    public Race<T> withTimeout(final long limit, final TimeUnit unit) {
        return new com.yegor256.together.race.Race<>(
            this.scenario, this.rules.withTimeout(limit, unit)
        );
    }

    /**
     * Stop fast.
     * @return New race
     */
    public Race<T> failFast() {
        return new com.yegor256.together.race.Race<>(
            this.scenario, this.rules.failFast()
        );
    }

    @Override
    public Iterator<T> iterator() {
        return new com.yegor256.together.support.Iter<>(this.asList());
    }

    /**
     * Convert to list.
     * @return Results
     */
    public List<T> asList() {
        final ExecutorService service = this.scenario.newService();
        try {
            return this.rules.resultsOf(this.scenario, service);
        } finally {
            new com.yegor256.together.support.Shutdown(service).finish();
        }
    }
}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import com.yegor256.together.policy.Watching;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * One round of the race.
 * @param <T> Type of result
 * @since 1.0
 */
public final class Round<T> {

    /**
     * Scenario.
     */
    private final Scenario<T> scenario;

    /**
     * Watching policy.
     */
    private final Watching watching;

    /**
     * Ctor.
     * @param origin Scenario
     * @param policy Watching policy
     */
    public Round(final Scenario<T> origin, final Watching policy) {
        this.scenario = origin;
        this.watching = policy;
    }

    /**
     * Execute the round.
     * @param service Executor service
     * @param number Round number
     * @return Results
     */
    public List<T> resultsOn(final ExecutorService service, final int number) {
        return this.watching.collectFrom(
            number,
            this.scenario.newCompleted(),
            this.scenario.startedOn(service)
        );
    }
}

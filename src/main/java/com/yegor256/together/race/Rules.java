/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import com.yegor256.together.policy.Watching;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Rules of the race.
 *
 * @since 1.0
 */
public final class Rules {

    /**
     * Rounds.
     */
    private final Rounds rounds;

    /**
     * Watching policy.
     */
    private final Watching watching;

    /**
     * Ctor.
     */
    public Rules() {
        this(
            new com.yegor256.together.race.Rounds(1),
            new com.yegor256.together.policy.Watching()
        );
    }

    /**
     * Private ctor.
     * @param every Rounds
     * @param policy Watching policy
     */
    private Rules(final Rounds every, final Watching policy) {
        this.rounds = every;
        this.watching = policy;
    }

    /**
     * Repeat more times.
     * @param total Number of rounds
     * @return New rules
     */
    public Rules repeated(final int total) {
        return new com.yegor256.together.race.Rules(
            new com.yegor256.together.race.Rounds(total), this.watching
        );
    }

    /**
     * Limit time.
     * @param limit Timeout
     * @param unit Unit
     * @return New rules
     */
    public Rules withTimeout(final long limit, final TimeUnit unit) {
        return new com.yegor256.together.race.Rules(
            this.rounds, this.watching.withTimeout(limit, unit)
        );
    }

    /**
     * Stop fast.
     * @return New rules
     */
    public Rules failFast() {
        return new com.yegor256.together.race.Rules(
            this.rounds, this.watching.failFast()
        );
    }

    /**
     * Execute all rounds.
     * @param scenario Scenario
     * @param service Executor service
     * @param <T> Type of result
     * @return Results
     */
    public <T> List<T> resultsOf(final Scenario<T> scenario,
        final ExecutorService service) {
        final List<T> results = new LinkedList<>();
        for (final Integer round : this.rounds) {
            results.addAll(
                new com.yegor256.together.race.Round<>(scenario, this.watching)
                    .resultsOn(service, round)
            );
        }
        return results;
    }
}

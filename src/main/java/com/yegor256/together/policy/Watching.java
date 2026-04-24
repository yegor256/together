/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Started;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Watching policy.
 *
 * @since 1.0
 */
public final class Watching {

    /**
     * Patience.
     */
    private final Patience patience;

    /**
     * Reaction.
     */
    private final Reaction reaction;

    /**
     * Ctor.
     */
    public Watching() {
        this(
            new com.yegor256.together.policy.Forever(),
            new com.yegor256.together.policy.WaitForAll()
        );
    }

    /**
     * Private ctor.
     * @param wait Patience
     * @param react Reaction
     */
    private Watching(final Patience wait, final Reaction react) {
        this.patience = wait;
        this.reaction = react;
    }

    /**
     * Add timeout.
     * @param limit Timeout
     * @param unit Unit
     * @return New policy
     */
    public Watching withTimeout(final long limit, final TimeUnit unit) {
        return new com.yegor256.together.policy.Watching(
            new com.yegor256.together.policy.Limited(limit, unit), this.reaction
        );
    }

    /**
     * Stop fast.
     * @return New policy
     */
    public Watching failFast() {
        return new com.yegor256.together.policy.Watching(
            this.patience, new com.yegor256.together.policy.StopFast()
        );
    }

    /**
     * Collect all results.
     * @param round Round number
     * @param completed Completed executions
     * @param started Started execution
     * @param <T> Type of result
     * @return Results
     */
    public <T> List<T> collectFrom(final int round, final Completed<T> completed,
        final Started<T> started) {
        started.start();
        while (completed.isIncomplete()) {
            this.reaction.reactTo(
                round, started,
                completed.with(this.patience.waitFor(completed, round, started))
            );
        }
        return completed.resultsIn(round);
    }
}

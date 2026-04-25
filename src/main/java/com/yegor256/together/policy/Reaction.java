/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Started;

/**
 * Reaction to the next completed execution.
 * @since 1.0
 */
@FunctionalInterface
public interface Reaction {

    /**
     * React to the next state.
     * @param round Round number
     * @param started Started execution
     * @param completed Completed executions
     * @param <T> Type of result
     */
    <T> void reactTo(int round, Started<T> started, Completed<T> completed);
}

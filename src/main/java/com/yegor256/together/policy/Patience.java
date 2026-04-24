/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Execution;
import com.yegor256.together.execution.Started;

/**
 * How long to wait.
 *
 * @since 1.0
 */
@FunctionalInterface
public interface Patience {

    /**
     * Wait for the next execution.
     * @param completed Completed executions
     * @param round Round number
     * @param started Started execution
     * @param <T> Type of result
     * @return Next execution
     */
    <T> Execution<T> waitFor(Completed<T> completed, int round, Started<T> started);
}

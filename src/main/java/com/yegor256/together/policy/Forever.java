/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Execution;
import com.yegor256.together.execution.Started;

/**
 * Wait forever.
 *
 * @since 1.0
 */
public final class Forever implements Patience {

    @Override
    public <T> Execution<T> waitFor(final Completed<T> completed, final int round,
        final Started<T> started) {
        return started.next(round);
    }
}

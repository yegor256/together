/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Started;

/**
 * Stop on the first failure.
 * @since 1.0
 */
public final class StopFast implements Reaction {

    @Override
    public <T> void reactTo(final int round, final Started<T> started,
        final Completed<T> completed) {
        completed.stopFastIn(round, started);
    }
}

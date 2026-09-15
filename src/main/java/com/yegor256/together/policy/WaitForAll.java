/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Started;

/**
 * Keep waiting for all threads.
 *
 * @since 1.0
 */
public final class WaitForAll implements Reaction {

    /**
     * Constructor.
     */
    public WaitForAll() {
        // Nothing to initialize.
    }

    @Override
    public <T> void reactTo(final int round, final Started<T> started,
        final Completed<T> completed) {
        // Intentionally empty.
    }
}

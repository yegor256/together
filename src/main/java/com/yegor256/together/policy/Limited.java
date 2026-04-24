/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Execution;
import com.yegor256.together.execution.Started;
import java.util.concurrent.TimeUnit;

/**
 * Limited waiting time.
 *
 * @since 1.0
 */
public final class Limited implements Patience {

    /**
     * Deadline.
     */
    private final Deadline deadline;

    /**
     * Ctor.
     * @param timeout Timeout
     * @param tunit Unit
     */
    public Limited(final long timeout, final TimeUnit tunit) {
        // @checkstyle ConstructorsCodeFreeCheck (1 line)
        this(new Deadline(timeout, tunit));
    }

    /**
     * Private ctor.
     * @param time Deadline
     */
    private Limited(final Deadline time) {
        this.deadline = time;
    }

    @Override
    public <T> Execution<T> waitFor(final Completed<T> completed, final int round,
        final Started<T> started) {
        return started.next(round, this.deadline, completed.missingThread());
    }
}

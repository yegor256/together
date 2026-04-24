/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import java.util.concurrent.TimeUnit;

/**
 * Timeout deadline.
 *
 * @since 1.0
 */
public final class Deadline {

    /**
     * Timeout.
     */
    private final long limit;

    /**
     * Unit.
     */
    private final TimeUnit unit;

    /**
     * Ctor.
     * @param timeout Timeout
     * @param tunit Time unit
     */
    public Deadline(final long timeout, final TimeUnit tunit) {
        // @checkstyle ConstructorsCodeFreeCheck (3 lines)
        if (timeout < 1L) {
            throw new IllegalArgumentException(
                String.format("Timeout must be positive: %d", timeout)
            );
        }
        this.limit = timeout;
        this.unit = tunit;
    }

    /**
     * Remaining timeout.
     * @param start Start time
     * @return Remaining timeout
     */
    public long from(final long start) {
        return Math.max(
            1L,
            this.limit - this.unit.convert(
                System.nanoTime() - start,
                TimeUnit.NANOSECONDS
            )
        );
    }

    /**
     * Time unit.
     * @return Unit
     */
    public TimeUnit timeUnit() {
        return this.unit;
    }
}

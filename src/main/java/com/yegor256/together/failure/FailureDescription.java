/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.failure;

import java.util.concurrent.TimeUnit;

/**
 * Failure description.
 * @since 1.0
 */
public final class FailureDescription {

    /**
     * Failure context.
     */
    private final FailureContext context;

    /**
     * Failure kind.
     */
    private final FailureKind kind;

    /**
     * Ctor.
     * @param details Failure context
     * @param failure Failure kind
     */
    public FailureDescription(final FailureContext details,
        final FailureKind failure) {
        this.context = details;
        this.kind = failure;
    }

    /**
     * Message text.
     * @return Message
     */
    public String messageText() {
        return this.context.messageFor(this.kind);
    }

    /**
     * Whether timeout caused this failure.
     * @return TRUE if timeout happened
     */
    public boolean causedByTimeout() {
        return this.kind.causedByTimeout();
    }

    /**
     * Whether it happened in this round.
     * @param number Round number
     * @return TRUE if yes
     */
    public boolean happenedInRound(final int number) {
        return this.context.happenedInRound(number);
    }

    /**
     * Whether it happened in this thread.
     * @param number Thread number
     * @return TRUE if yes
     */
    public boolean happenedInThread(final int number) {
        return this.context.happenedInThread(number);
    }

    /**
     * Whether it lasted at least this long.
     * @param duration Duration
     * @param unit Time unit
     * @return TRUE if yes
     */
    public boolean lastedAtLeast(final long duration, final TimeUnit unit) {
        return this.context.lastedAtLeast(duration, unit);
    }
}

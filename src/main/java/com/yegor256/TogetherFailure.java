/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import com.yegor256.together.failure.FailureContext;
import com.yegor256.together.failure.FailureDescription;
import com.yegor256.together.failure.FailureKind;
import java.util.concurrent.TimeUnit;

/**
 * Failure of one concurrent execution.
 *
 * @since 1.0
 */
@SuppressWarnings("serial")
public final class TogetherFailure extends IllegalArgumentException {

    /**
     * Failure description.
     */
    private final FailureDescription description;

    /**
     * Ctor.
     * @param details Failure context
     * @param failure Failure kind
     * @param cause Root cause
     */
    // @checkstyle ConstructorsCodeFreeCheck (1 line)
    public TogetherFailure(final FailureContext details,
        final FailureKind failure, final Throwable cause) {
        super("", cause);
        this.description = new com.yegor256.together.failure.FailureDescription(
            details, failure
        );
    }

    /**
     * Whether timeout caused this failure.
     * @return TRUE if timeout happened
     */
    public boolean causedByTimeout() {
        return this.description.causedByTimeout();
    }

    /**
     * Whether it happened in this round.
     * @param number Round number
     * @return TRUE if yes
     */
    public boolean happenedInRound(final int number) {
        return this.description.happenedInRound(number);
    }

    /**
     * Whether it happened in this thread.
     * @param number Thread number
     * @return TRUE if yes
     */
    public boolean happenedInThread(final int number) {
        return this.description.happenedInThread(number);
    }

    /**
     * Whether it lasted at least this long.
     * @param duration Duration
     * @param unit Time unit
     * @return TRUE if yes
     */
    public boolean lastedAtLeast(final long duration, final TimeUnit unit) {
        return this.description.lastedAtLeast(duration, unit);
    }

    @Override
    public String getMessage() {
        return this.description.messageText();
    }
}

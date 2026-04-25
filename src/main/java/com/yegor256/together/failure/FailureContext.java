/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.failure;

import java.util.concurrent.TimeUnit;

/**
 * Failure context.
 * @since 1.0
 */
public final class FailureContext {

    /**
     * Round number.
     */
    private final int round;

    /**
     * Thread number.
     */
    private final int thread;

    /**
     * Elapsed time in milliseconds.
     */
    private final long elapsed;

    /**
     * Ctor.
     * @param when Round number
     * @param threadnum Thread number
     * @param msec Elapsed time
     */
    public FailureContext(final int when, final int threadnum, final long msec) {
        this.round = when;
        this.thread = threadnum;
        this.elapsed = msec;
    }

    /**
     * Whether it happened in this round.
     * @param number Round number
     * @return TRUE if yes
     */
    public boolean happenedInRound(final int number) {
        return this.round == number;
    }

    /**
     * Whether it happened in this thread.
     * @param number Thread number
     * @return TRUE if yes
     */
    public boolean happenedInThread(final int number) {
        return this.thread == number;
    }

    /**
     * Whether it lasted at least this long.
     * @param duration Duration
     * @param unit Time unit
     * @return TRUE if yes
     */
    public boolean lastedAtLeast(final long duration, final TimeUnit unit) {
        return this.elapsed >= TimeUnit.MILLISECONDS.convert(duration, unit);
    }

    /**
     * Build failure message.
     * @param kind Failure kind
     * @return Message
     */
    public String messageFor(final FailureKind kind) {
        String text = String.format(
            "Round #%d failed in thread #%d after %dms",
            this.round, this.thread, this.elapsed
        );
        if (kind.causedByTimeout()) {
            text = String.format("%s because timeout was exceeded", text);
        } else if (kind.hasMessage()) {
            text = String.format("%s: %s", text, kind.messageText());
        }
        return text;
    }
}

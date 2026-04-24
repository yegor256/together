/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.failure;

/**
 * Failure kind.
 *
 * @since 1.0
 */
public final class FailureKind {

    /**
     * Whether timeout caused it.
     */
    private final boolean timeout;

    /**
     * Root message.
     */
    private final String message;

    /**
     * Ctor.
     * @param exceeded Whether timeout happened
     * @param text Root message
     */
    public FailureKind(final boolean exceeded, final String text) {
        this.timeout = exceeded;
        this.message = text;
    }

    /**
     * Whether timeout caused this failure.
     * @return TRUE if timeout happened
     */
    public boolean causedByTimeout() {
        return this.timeout;
    }

    /**
     * Whether the cause has message.
     * @return TRUE if it has one
     */
    public boolean hasMessage() {
        return this.message != null;
    }

    /**
     * Message text.
     * @return Message
     */
    public String messageText() {
        String text = "";
        if (this.message != null) {
            text = this.message;
        }
        return text;
    }
}

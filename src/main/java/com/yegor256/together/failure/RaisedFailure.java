/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.failure;

import com.yegor256.TogetherFailure;

/**
 * Raised failure.
 * @since 1.0
 */
public final class RaisedFailure {

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
    public RaisedFailure(final FailureContext details, final FailureKind failure) {
        this.context = details;
        this.kind = failure;
    }

    /**
     * Convert to public failure.
     * @param cause Root cause
     * @return Failure
     */
    public TogetherFailure asPublic(final Throwable cause) {
        return new TogetherFailure(this.context, this.kind, cause);
    }
}

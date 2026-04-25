/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TogetherFailure}.
 * @since 1.0
 */
final class TogetherFailureTest {

    /**
     * Error text.
     */
    private static final String TEXT = "boom";

    /**
     * Failure context.
     */
    private static final com.yegor256.together.failure.FailureContext CONTEXT =
        new com.yegor256.together.failure.FailureContext(2, 7, 15L);

    @Test
    void recognizesTimeout() {
        MatcherAssert.assertThat(
            "must recognize timeout",
            new TogetherFailure(
                TogetherFailureTest.CONTEXT,
                new com.yegor256.together.failure.FailureKind(
                    true, TogetherFailureTest.TEXT
                ),
                new IllegalStateException(TogetherFailureTest.TEXT)
            ).causedByTimeout(),
            Matchers.is(true)
        );
    }

    @Test
    void recognizesRound() {
        MatcherAssert.assertThat(
            "must recognize round",
            new TogetherFailure(
                TogetherFailureTest.CONTEXT,
                new com.yegor256.together.failure.FailureKind(
                    true, TogetherFailureTest.TEXT
                ),
                new IllegalStateException(TogetherFailureTest.TEXT)
            ).happenedInRound(2),
            Matchers.is(true)
        );
    }

    @Test
    void recognizesThread() {
        MatcherAssert.assertThat(
            "must recognize thread",
            new TogetherFailure(
                TogetherFailureTest.CONTEXT,
                new com.yegor256.together.failure.FailureKind(
                    true, TogetherFailureTest.TEXT
                ),
                new IllegalStateException(TogetherFailureTest.TEXT)
            ).happenedInThread(7),
            Matchers.is(true)
        );
    }

    @Test
    void comparesElapsedTime() {
        MatcherAssert.assertThat(
            "must compare elapsed time",
            new TogetherFailure(
                TogetherFailureTest.CONTEXT,
                new com.yegor256.together.failure.FailureKind(
                    true, TogetherFailureTest.TEXT
                ),
                new IllegalStateException(TogetherFailureTest.TEXT)
            ).lastedAtLeast(10L, TimeUnit.MILLISECONDS),
            Matchers.is(true)
        );
    }

    @Test
    void rejectsDifferentFailureLocation() {
        final TogetherFailure failure = new TogetherFailure(
            TogetherFailureTest.CONTEXT,
            new com.yegor256.together.failure.FailureKind(false, (String) null),
            new IllegalStateException(TogetherFailureTest.TEXT)
        );
        MatcherAssert.assertThat(
            "must reject different round and thread",
            failure.causedByTimeout()
                || failure.happenedInRound(3)
                || failure.happenedInThread(8)
                || failure.lastedAtLeast(16L, TimeUnit.MILLISECONDS),
            Matchers.is(false)
        );
    }
}

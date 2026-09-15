/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.failure;

import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link FailureContext}.
 *
 * @since 1.0
 */
final class FailureContextTest {

    @Test
    void recognizesRoundAndThread() {
        final FailureContext context = new FailureContext(2, 7, 15L);
        MatcherAssert.assertThat(
            "must recognize round",
            context.happenedInRound(2) && context.happenedInThread(7),
            Matchers.is(true)
        );
    }

    @Test
    void rejectsDifferentRoundAndThread() {
        final FailureContext context = new FailureContext(2, 7, 15L);
        MatcherAssert.assertThat(
            "must reject different round and thread",
            context.happenedInRound(3) || context.happenedInThread(8),
            Matchers.is(false)
        );
    }

    @Test
    void comparesElapsedTime() {
        MatcherAssert.assertThat(
            "must compare elapsed time",
            new FailureContext(2, 7, 15L).lastedAtLeast(
                10L, TimeUnit.MILLISECONDS
            ),
            Matchers.is(true)
        );
    }

    @Test
    void rejectsLongerElapsedTime() {
        MatcherAssert.assertThat(
            "must reject larger duration",
            new FailureContext(2, 7, 15L).lastedAtLeast(
                16L, TimeUnit.MILLISECONDS
            ),
            Matchers.is(false)
        );
    }

    @Test
    void buildsMessageForTimeout() {
        MatcherAssert.assertThat(
            "must describe timeout",
            new FailureContext(2, 7, 15L).messageFor(
                new FailureKind(true, "boom")
            ),
            Matchers.containsString("timeout was exceeded")
        );
    }

    @Test
    void buildsMessageForRegularFailure() {
        MatcherAssert.assertThat(
            "must describe regular failure",
            new FailureContext(2, 7, 15L).messageFor(
                new FailureKind(false, "boom")
            ),
            Matchers.containsString(": boom")
        );
    }
}

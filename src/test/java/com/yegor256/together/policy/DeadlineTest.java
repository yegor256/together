/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Deadline}.
 * @since 1.0
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
final class DeadlineTest {

    @Test
    void keepsPositiveRemainingTimeout() {
        final Deadline deadline = new Deadline(50L, TimeUnit.MILLISECONDS);
        final long left = deadline.from(System.nanoTime());
        MatcherAssert.assertThat(
            "must keep timeout positive",
            left > 0L,
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "must not increase timeout",
            left <= 50L,
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "must keep time unit",
            deadline.timeUnit(),
            Matchers.equalTo(TimeUnit.MILLISECONDS)
        );
    }
}

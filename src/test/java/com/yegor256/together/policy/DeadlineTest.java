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
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
final class DeadlineTest {

    @Test
    void expiresWithoutAnAdditionalUnit() {
        MatcherAssert.assertThat(
            "must not renew an expired deadline",
            new Deadline(1L, TimeUnit.MINUTES).from(
                System.nanoTime() - TimeUnit.MINUTES.toNanos(2L)
            ),
            Matchers.equalTo(0L)
        );
    }

    @Test
    void preservesRemainingNanoseconds() {
        MatcherAssert.assertThat(
            "must retain the fractional minute in nanoseconds",
            new Deadline(1L, TimeUnit.MINUTES).from(
                System.nanoTime() - TimeUnit.SECONDS.toNanos(59L)
            ),
            Matchers.both(Matchers.greaterThan(TimeUnit.MILLISECONDS.toNanos(500L)))
                .and(Matchers.lessThanOrEqualTo(TimeUnit.SECONDS.toNanos(1L)))
        );
    }

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
            left <= TimeUnit.MILLISECONDS.toNanos(50L),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "must keep configured time unit",
            deadline.timeUnit(),
            Matchers.equalTo(TimeUnit.MILLISECONDS)
        );
    }
}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.TogetherFailure;
import com.yegor256.together.race.Scenario;
import com.yegor256.together.race.Threads;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Limited}.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class LimitedTest {

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void failsOnTimeout() {
        final Scenario<Integer> scenario = new Scenario<>(
            new Threads(1),
            thread -> {
                Thread.sleep(100L);
                return thread;
            }
        );
        final ExecutorService service = scenario.newService();
        try {
            final com.yegor256.together.execution.Started<Integer> started =
                scenario.startedOn(service);
            started.start();
            final TogetherFailure failure = Assertions.assertThrows(
                TogetherFailure.class,
                () -> new Limited(10L, TimeUnit.MILLISECONDS).waitFor(
                    scenario.newCompleted(), 0, started
                ),
                "must fail on timeout"
            );
            MatcherAssert.assertThat(
                "must mark timeout failures",
                failure.causedByTimeout(),
                Matchers.is(true)
            );
        } finally {
            new com.yegor256.together.support.Shutdown(service).finish();
        }
    }

    @Test
    void rejectsNonPositiveTimeout() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Limited(0L, TimeUnit.MILLISECONDS),
            "must reject invalid timeout"
        );
    }
}

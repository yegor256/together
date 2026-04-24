/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.race.Scenario;
import com.yegor256.together.race.Threads;
import java.util.concurrent.ExecutorService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Forever}.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class ForeverTest {

    @Test
    void waitsForNextExecutionWithoutTimeout() {
        final Scenario<Integer> scenario =
            new Scenario<>(new Threads(1), thread -> thread + 5);
        try (ExecutorService service = scenario.newService()) {
            final com.yegor256.together.execution.Started<Integer> started =
                scenario.startedOn(service);
            started.start();
            MatcherAssert.assertThat(
                "must return next execution",
                scenario.newCompleted()
                    .with(new Forever().waitFor(scenario.newCompleted(), 0, started))
                    .resultsIn(0),
                Matchers.contains(5)
            );
        }
    }
}

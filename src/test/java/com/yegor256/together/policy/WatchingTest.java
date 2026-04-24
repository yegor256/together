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
 * Test case for {@link Watching}.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class WatchingTest {

    @Test
    void collectsResultsFromStartedExecution() {
        final Scenario<Integer> scenario =
            new Scenario<>(new Threads(2), thread -> thread);
        try (ExecutorService service = scenario.newService()) {
            MatcherAssert.assertThat(
                "must collect all results",
                new Watching().collectFrom(
                    0,
                    scenario.newCompleted(),
                    scenario.startedOn(service)
                ),
                Matchers.contains(0, 1)
            );
        }
    }
}

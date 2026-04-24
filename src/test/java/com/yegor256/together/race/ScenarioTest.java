/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Scenario}.
 *
 * @since 1.0
 */
final class ScenarioTest {

    @Test
    @SuppressWarnings({"PMD.UnnecessaryLocalRule", "PMD.CloseResource"})
    void createsStartedAndCompletedObjects() {
        final Threads threads = new Threads(1);
        final Scenario<Integer> scenario =
            new Scenario<>(threads, thread -> thread + 1);
        final java.util.concurrent.ExecutorService service = scenario.newService();
        try {
            final com.yegor256.together.execution.Started<Integer> started =
                scenario.startedOn(service);
            started.start();
            MatcherAssert.assertThat(
                "must execute submitted job",
                scenario.newCompleted().with(started.next(0)).resultsIn(0),
                Matchers.contains(1)
            );
        } finally {
            new com.yegor256.together.support.Shutdown(service).finish();
        }
    }
}

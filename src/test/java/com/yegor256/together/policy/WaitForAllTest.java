/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Execution;
import com.yegor256.together.execution.Started;
import com.yegor256.together.race.Threads;
import java.util.HashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link WaitForAll}.
 * @since 1.0
 */
final class WaitForAllTest {

    @Test
    void leavesCompletedResultsUntouched() {
        final Completed<Integer> completed = new Completed<Integer>(new Threads(1))
            .with(new Execution<Integer>(0, 7, 0L));
        new WaitForAll().reactTo(
            0,
            new Started<>(
                new java.util.concurrent.CountDownLatch(0),
                new ExecutorCompletionService<>(Executors.newSingleThreadExecutor()),
                new HashMap<>()
            ),
            completed
        );
        MatcherAssert.assertThat(
            "must keep collected results",
            completed.resultsIn(0),
            Matchers.contains(7)
        );
    }
}

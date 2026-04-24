/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import com.yegor256.TogetherFailure;
import com.yegor256.together.race.Threads;
import java.util.HashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Completed}.
 *
 * @since 1.0
 */
final class CompletedTest {

    @Test
    void collectsAndOrdersResults() {
        MatcherAssert.assertThat(
            "must collect results in thread order",
            new Completed<Integer>(new Threads(2))
                .with(new Execution<>(1, 11, 0L))
                .with(new Execution<>(0, 7, 0L))
                .resultsIn(0),
            Matchers.contains(7, 11)
        );
    }

    @Test
    void stopsFastOnFailure() {
        Assertions.assertThrows(
            TogetherFailure.class,
            () -> new Completed<Integer>(new Threads(1))
                .with(new Execution<>(0, new IllegalStateException("boom"), 0L))
                .stopFastIn(
                    0,
                    new Started<>(
                        new java.util.concurrent.CountDownLatch(0),
                        new ExecutorCompletionService<>(Executors.newSingleThreadExecutor()),
                        new HashMap<>()
                    )
                ),
            "must stop on failure"
        );
    }
}

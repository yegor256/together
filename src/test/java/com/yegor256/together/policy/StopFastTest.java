/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.policy;

import com.yegor256.TogetherFailure;
import com.yegor256.together.execution.Completed;
import com.yegor256.together.execution.Execution;
import com.yegor256.together.execution.Started;
import com.yegor256.together.race.Threads;
import java.util.HashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link StopFast}.
 * @since 1.0
 */
final class StopFastTest {

    @Test
    void throwsOnFirstFailure() {
        Assertions.assertThrows(
            TogetherFailure.class,
            () -> new StopFast().reactTo(
                0,
                new Started<>(
                    new java.util.concurrent.CountDownLatch(0),
                    new ExecutorCompletionService<>(Executors.newSingleThreadExecutor()),
                    new HashMap<>()
                ),
                new Completed<Integer>(new Threads(1)).with(
                    new Execution<>(0, new IllegalStateException("boom"), 0L)
                )
            ),
            "must throw on the first failure"
        );
    }
}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import com.yegor256.TogetherFailure;
import com.yegor256.together.policy.Deadline;
import java.util.HashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Started}.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class StartedTest {

    @Test
    void failsWhenTimedOut() {
        final Started<Integer> started = new Started<>(
            new java.util.concurrent.CountDownLatch(1),
            new ExecutorCompletionService<>(Executors.newSingleThreadExecutor()),
            new HashMap<>()
        );
        started.start();
        final TogetherFailure failure = Assertions.assertThrows(
            TogetherFailure.class,
            () -> started.next(0, new Deadline(1L, TimeUnit.MILLISECONDS), 0),
            "must fail on timeout"
        );
        MatcherAssert.assertThat(
            "must mark timeout",
            failure.causedByTimeout(),
            Matchers.is(true)
        );
    }
}

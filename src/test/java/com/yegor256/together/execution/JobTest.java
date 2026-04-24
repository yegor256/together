/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Job}.
 *
 * @since 1.0
 */
final class JobTest {

    @Test
    void executesAction() throws Exception {
        final Job<Integer> job = new Job<>(
            new CountDownLatch(0),
            3,
            thread -> thread * 2
        );
        final List<Integer> results = new java.util.ArrayList<>(1);
        job.call().complete(0, results);
        MatcherAssert.assertThat(
            "must execute action result",
            results,
            Matchers.contains(6)
        );
    }

    @Test
    @SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "PMD.CloseResource"})
    void waitsForLatchBeforeExecution() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            final Future<Execution<Integer>> future = service.submit(
                new Job<>(latch, 3, thread -> thread * 2)
            );
            MatcherAssert.assertThat(
                "must wait for latch release",
                future.isDone(),
                Matchers.is(false)
            );
            latch.countDown();
            final List<Integer> results = new java.util.ArrayList<>(1);
            future.get().complete(0, results);
            MatcherAssert.assertThat(
                "must execute after latch release",
                results,
                Matchers.contains(6)
            );
        } finally {
            new com.yegor256.together.support.Shutdown(service).finish();
        }
    }
}

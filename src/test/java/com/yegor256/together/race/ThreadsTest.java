/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import com.yegor256.together.execution.Execution;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Threads}.
 * @since 1.0
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
final class ThreadsTest {

    @Test
    void managesThreadLayout() {
        final Threads threads = new Threads(3);
        final Map<Integer, Execution<Integer>> done = new HashMap<>();
        done.put(0, new Execution<>(0, 0, 0L));
        done.put(2, new Execution<>(2, 2, 0L));
        MatcherAssert.assertThat(
            "must find missing thread",
            threads.firstAbsentFrom(new HashMap<Integer, Execution<?>>(done)),
            Matchers.equalTo(1)
        );
        final java.util.List<Integer> results = new java.util.ArrayList<>(3);
        done.put(1, new Execution<>(1, 1, 0L));
        threads.appendTo(done, 0, results);
        MatcherAssert.assertThat(
            "must append results in order",
            results,
            Matchers.contains(0, 1, 2)
        );
    }

    @Test
    void iteratesAllThreadsAndRecognizesCompleteState() {
        final Threads threads = new Threads(3);
        final Map<Integer, Execution<?>> done = new HashMap<>();
        done.put(0, new Execution<>(0, 0, 0L));
        done.put(1, new Execution<>(1, 1, 0L));
        done.put(2, new Execution<>(2, 2, 0L));
        MatcherAssert.assertThat(
            "must keep all thread numbers",
            new java.util.ArrayList<Integer>(
                Arrays.asList(
                    threads.inRandomOrder().iterator().next(),
                    threads.inRandomOrder().iterator().next(),
                    threads.inRandomOrder().iterator().next()
                )
            ).size(),
            Matchers.equalTo(3)
        );
        MatcherAssert.assertThat(
            "must recognize complete state",
            threads.misses(done),
            Matchers.is(false)
        );
    }
}

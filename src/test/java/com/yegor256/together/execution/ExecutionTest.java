/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import com.yegor256.TogetherFailure;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Execution}.
 * @since 1.0
 */
final class ExecutionTest {

    @Test
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    void placesAndCompletesSuccessfulExecution() {
        final Map<Integer, Execution<Integer>> all = new java.util.TreeMap<>();
        final List<Integer> results = new java.util.ArrayList<>(1);
        final Execution<Integer> execution = new Execution<>(0, 42, 1L);
        execution.placeInto(all);
        execution.complete(0, results);
        MatcherAssert.assertThat(
            "must place and complete value",
            results,
            Matchers.contains(42)
        );
    }

    @Test
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    void convertsFailureToPublicException() {
        final TogetherFailure failure = Assertions.assertThrows(
            TogetherFailure.class,
            () -> new Execution<Integer>(
                3, new IllegalStateException("boom"), 2L
            ).complete(5, new java.util.ArrayList<Integer>(1)),
            "must convert failure to public exception"
        );
        MatcherAssert.assertThat(
            "must keep round information",
            failure.happenedInRound(5),
            Matchers.is(true)
        );
    }
}

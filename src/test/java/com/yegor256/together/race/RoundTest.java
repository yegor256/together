/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import java.util.concurrent.ExecutorService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Round}.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class RoundTest {

    @Test
    void executesOneRound() {
        final Threads threads = new Threads(2);
        try (ExecutorService service = threads.newService()) {
            MatcherAssert.assertThat(
                "must execute one round only",
                new Round<>(
                    new Scenario<>(threads, thread -> thread),
                    new com.yegor256.together.policy.Watching()
                ).resultsOn(service, 0),
                Matchers.contains(0, 1)
            );
        }
    }
}

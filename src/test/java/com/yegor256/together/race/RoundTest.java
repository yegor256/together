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
 * @since 1.0
 */
final class RoundTest {

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void executesOneRound() {
        final Threads threads = new Threads(2);
        final ExecutorService service = threads.newService();
        try {
            MatcherAssert.assertThat(
                "must execute one round only",
                new Round<>(
                    new Scenario<>(threads, thread -> thread),
                    new com.yegor256.together.policy.Watching()
                ).resultsOn(service, 0),
                Matchers.contains(0, 1)
            );
        } finally {
            new com.yegor256.together.support.Shutdown(service).finish();
        }
    }
}

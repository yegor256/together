/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Race}.
 * @since 1.0
 */
final class RaceTest {

    @Test
    void runsConfiguredRace() {
        MatcherAssert.assertThat(
            "must execute all rounds",
            new Race<>(
                2,
                thread -> thread
            ).repeated(2).withTimeout(1L, TimeUnit.SECONDS).failFast().asList(),
            Matchers.contains(0, 1, 0, 1)
        );
    }
}

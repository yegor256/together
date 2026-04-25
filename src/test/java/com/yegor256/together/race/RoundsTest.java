/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Rounds}.
 * @since 1.0
 */
final class RoundsTest {

    @Test
    void iteratesThroughAllRounds() {
        MatcherAssert.assertThat(
            "must expose all round numbers",
            new Rounds(3),
            Matchers.contains(0, 1, 2)
        );
    }

    @Test
    void rejectsNonPositiveCount() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Rounds(0),
            "must reject invalid number of rounds"
        );
    }
}

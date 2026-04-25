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
 * Test case for {@link Rules}.
 * @since 1.0
 */
final class RulesTest {

    @Test
    void appliesRulesToScenario() {
        MatcherAssert.assertThat(
            "must repeat scenario results",
            new Rules().repeated(2).withTimeout(1L, TimeUnit.SECONDS).failFast().resultsOf(
                new Scenario<>(new Threads(2), thread -> thread),
                new Threads(2).newService()
            ),
            Matchers.contains(0, 1, 0, 1)
        );
    }
}

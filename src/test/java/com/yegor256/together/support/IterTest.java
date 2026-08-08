/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.support;

import java.util.Arrays;
import java.util.LinkedList;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Iter}.
 * @since 1.0
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
final class IterTest {

    @Test
    void iteratesAndPrintsItems() {
        final Iter<Integer> iter = new Iter<>(new LinkedList<>(Arrays.asList(1, 2)));
        MatcherAssert.assertThat(
            "must print all items",
            iter.toString(),
            Matchers.equalTo("[1, 2]")
        );
        MatcherAssert.assertThat(
            "must iterate through all items",
            new LinkedList<>(Arrays.asList(iter.next(), iter.next())),
            Matchers.contains(1, 2)
        );
        MatcherAssert.assertThat(
            "must finish iteration",
            iter.hasNext(),
            Matchers.is(false)
        );
    }
}

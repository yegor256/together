/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.race;

import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Round numbers.
 *
 * @since 1.0
 */
public final class Rounds implements Iterable<Integer> {

    /**
     * Total rounds.
     */
    private final int total;

    /**
     * Ctor.
     * @param count Total rounds
     */
    public Rounds(final int count) {
        // @checkstyle ConstructorsCodeFreeCheck (3 lines)
        if (count < 1) {
            throw new IllegalArgumentException(
                String.format("Number of rounds must be positive: %d", count)
            );
        }
        this.total = count;
    }

    @Override
    public Iterator<Integer> iterator() {
        return IntStream.range(0, this.total).boxed().iterator();
    }
}

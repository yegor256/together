/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.yegor256;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cactoos.set.SetOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Together}.
 *
 * @since 0.1.0
 */
final class TogetherTest {

    @Test
    void runsSimpleTest() {
        MatcherAssert.assertThat(
            "fails to collect all numbers",
            new Together<>(
                t -> 1
            ),
            Matchers.not(Matchers.hasItem(Matchers.equalTo(0)))
        );
    }

    @Test
    void returnsUniqueThreadNumbers() {
        final int threads = 10;
        MatcherAssert.assertThat(
            "fails to collect unique numbers",
            new SetOf<>(
                new Together<>(
                    threads,
                    t -> t
                ).asList()
            ).size(),
            Matchers.equalTo(threads)
        );
    }

    @Test
    void printsNiceString() {
        MatcherAssert.assertThat(
            "fails to print toString",
            new Together<>(
                t -> t
            ).iterator().toString(),
            Matchers.hasToString(Matchers.containsString("2"))
        );
    }

    @Test
    void printsOneElementToString() {
        MatcherAssert.assertThat(
            "fails to print one-element toString",
            new Together<>(
                1,
                t -> t
            ).iterator().toString(),
            Matchers.hasToString("[0]")
        );
    }

    @Test
    void failsInAllThreads() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Together<>(
                t -> {
                    throw new IllegalArgumentException("intended");
                }
            ).asList(),
            "fails because of failure in lambda"
        );
    }

    @RepeatedTest(5)
    void overlapsThreads() {
        final AtomicBoolean finished = new AtomicBoolean(false);
        MatcherAssert.assertThat(
            "fails to start them parallel",
            new Together<>(
                2,
                t -> {
                    if (finished.get()) {
                        throw new IllegalStateException("why?");
                    }
                    Thread.sleep(1L);
                    finished.set(true);
                    return t;
                }
            ).asList().size(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void startsInRandomOrder() {
        final int threads = 10;
        MatcherAssert.assertThat(
            "fails to start them parallel, in random order",
            new Together<>(
                threads,
                t -> t
            ).iterator().toString(),
            Matchers.not(
                Matchers.hasToString(
                    Matchers.containsString(
                        IntStream.rangeClosed(0, threads - 1)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining(", "))
                    )
                )
            )
        );
    }

}

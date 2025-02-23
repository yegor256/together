/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2024-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
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
            "doesn't fail when one of the threads fails"
        );
    }

    @Test
    void interruptsCurrentThreadCorrectly() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Thread main = new Thread(
            () -> {
                try {
                    new Together<>(
                        1,
                        t -> {
                            latch.countDown();
                            Thread.sleep(100_000L);
                            return t;
                        }
                    ).asList();
                } catch (final IllegalArgumentException ex) {
                    MatcherAssert.assertThat(
                        "doesn't interrupt the main thread",
                        Thread.currentThread().isInterrupted(),
                        Matchers.is(true)
                    );
                }
            }
        );
        main.start();
        latch.await();
        main.interrupt();
        main.join(100L, 0);
        MatcherAssert.assertThat(
            "failed to stop the thread",
            main.isAlive(),
            Matchers.is(false)
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

    @RepeatedTest(10)
    void startsInRandomOrder() {
        final int threads = 100;
        final CopyOnWriteArrayList<Integer> seen = new CopyOnWriteArrayList<>();
        new Together<>(
            threads,
            t -> {
                seen.add(t);
                return t;
            }
        ).asList();
        MatcherAssert.assertThat(
            "fails to start them parallel, in random order",
            seen,
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

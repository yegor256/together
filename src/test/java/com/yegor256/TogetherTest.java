/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.cactoos.set.SetOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Together}.
 *
 * @since 0.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.UnnecessaryLocalRule"})
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
        final TogetherFailure failure = Assertions.assertThrows(
            TogetherFailure.class,
            () -> new Together<>(
                t -> {
                    throw new IllegalArgumentException("intended");
                }
            ).asList(),
            "doesn't fail when one of the threads fails"
        );
        MatcherAssert.assertThat(
            "must report the failed round",
            failure.happenedInRound(0),
            Matchers.is(true)
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

    @Test
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
        final int threads = 100;
        final AtomicInteger counter = new AtomicInteger();
        MatcherAssert.assertThat(
            "fails to start them parallel, in random order",
            new Together<>(
                threads,
                t -> counter.getAndIncrement()
            ).asList(),
            Matchers.not(
                Matchers.contains(
                    IntStream.range(0, threads)
                        .boxed()
                        .toArray(Integer[]::new)
                )
            )
        );
    }

    @Test
    void returnsResultsInCorrectOrder() {
        final int threads = 100;
        MatcherAssert.assertThat(
            "the results should be returned in the order of thread indices",
            new Together<>(
                threads,
                t -> t
            ).asList(),
            Matchers.contains(
                IntStream.range(0, threads)
                    .boxed()
                    .toArray(Integer[]::new)
            )
        );
    }

    @Test
    void repeatsAllRounds() {
        MatcherAssert.assertThat(
            "must execute the same race more than once",
            new Together<>(
                3,
                t -> t
            ).repeated(2).asList(),
            Matchers.contains(0, 1, 2, 0, 1, 2)
        );
    }

    @Test
    void failsWhenRoundTimeoutIsExceeded() {
        final TogetherFailure failure = Assertions.assertThrows(
            TogetherFailure.class,
            () -> new Together<>(
                1,
                t -> {
                    Thread.sleep(100L);
                    return t;
                }
            ).withTimeout(10L, TimeUnit.MILLISECONDS).asList(),
            "must fail when a round exceeds timeout"
        );
        MatcherAssert.assertThat(
            "must mark timeout failures clearly",
            failure.causedByTimeout(),
            Matchers.is(true)
        );
    }

    @Test
    void interruptsRemainingThreadsInFailFastMode() throws InterruptedException {
        final CountDownLatch entered = new CountDownLatch(1);
        final CountDownLatch interrupted = new CountDownLatch(1);
        Assertions.assertThrows(
            TogetherFailure.class,
            () -> new Together<>(
                2,
                t -> {
                    if (t == 0) {
                        entered.await(1L, TimeUnit.SECONDS);
                        throw new IllegalStateException("boom");
                    }
                    try {
                        entered.countDown();
                        Thread.sleep(100_000L);
                    } catch (final InterruptedException ex) {
                        interrupted.countDown();
                        throw ex;
                    }
                    return t;
                }
            ).failFast().asList(),
            "must fail fast when one thread breaks"
        );
        entered.await(1L, TimeUnit.SECONDS);
        MatcherAssert.assertThat(
            "must interrupt the remaining thread",
            interrupted.await(1L, TimeUnit.SECONDS),
            Matchers.is(true)
        );
    }
}

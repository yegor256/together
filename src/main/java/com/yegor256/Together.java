/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import com.yegor256.together.race.Race;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Runs lambda function in multiple threads.
 *
 * <p>Use it like this, in your JUnit5 test (with Hamcrest):</p>
 *
 * <code><pre> import org.hamcrest.MatcherAssert;
 * import org.hamcrest.Matchers;
 * import org.junit.jupiter.api.Test;
 * import org.junit.jupiter.api.io.TempDir;
 * import com.yegor256.Together;
 *
 * class FooTest {
 *   &#64;Test
 *   void worksAsExpected() {
 *     MatcherAssert.assertThat(
 *       "processes all lambdas successfully",
 *       new Together&lt;&gt;(
 *         () -&gt; {
 *           // do the job
 *           return true;
 *         }
 *       ),
 *       Matchers.not(Matchers.hasItem(Matchers.is(false)))
 *     );
 *   }
 * }</pre></code>
 *
 * <p>Here, the {@link Together} class will run the "job" in multiple threads
 * and will make sure that all of them return {@code true}. If at least
 * one of them returns {@code false}, the test will fail. If at least one of the
 * threads will throw an exception, the test will also fail.</p>
 *
 * <p>{@link Together} guarantees that all threads will start exactly
 * simultaneously,
 * thus simulating race condition as much as it's possible. This is exactly
 * what you need for your tests: making sure your object under test
 * experiences troubles that are very similar to what it might experience
 * in real life.</p>
 *
 * @param <T> The type of result
 * @see <a href="https://www.yegor256.com/2018/03/27/how-to-test-thread-safety.html">How I Test My Java Classes for Thread-Safety</a>
 * @see <a href="https://junit.org/junit5/">JUnit5</a>
 * @see <a href="http://hamcrest.org">Hamcrest</a>
 * @see <a href="https://en.wikipedia.org/wiki/Race_condition">Race condition</a>
 * @see <a href="https://github.com/yegor256/together">GitHub repository</a>
 * @since 0.0.1
 */
public final class Together<T> implements Iterable<T> {

    /**
     * Race to execute.
     */
    private final Race<T> race;

    /**
     * Ctor.
     * @param act The action
     */
    public Together(final Together.Action<T> act) {
        this(new Race<>(act));
    }

    /**
     * Ctor.
     * @param total The number of threads
     * @param act The action
     */
    public Together(final int total, final Together.Action<T> act) {
        this(new Race<>(total, act));
    }

    /**
     * Private ctor.
     * @param origin Race to execute
     */
    private Together(final Race<T> origin) {
        this.race = origin;
    }

    /**
     * Repeat each race condition many times.
     * @param total Number of rounds
     * @return New instance
     * @since 1.0
     */
    public Together<T> repeated(final int total) {
        return new com.yegor256.Together<>(this.race.repeated(total));
    }

    /**
     * Limit the execution time of every round.
     * @param limit Timeout limit
     * @param unit Timeout unit
     * @return New instance
     * @since 1.0
     */
    public Together<T> withTimeout(final long limit, final TimeUnit unit) {
        return new com.yegor256.Together<>(this.race.withTimeout(limit, unit));
    }

    /**
     * Stop waiting as soon as one thread fails.
     * @return New instance
     * @since 1.0
     */
    public Together<T> failFast() {
        return new com.yegor256.Together<>(this.race.failFast());
    }

    @Override
    public Iterator<T> iterator() {
        return this.race.iterator();
    }

    /**
     * Turn it into a list.
     * @return The list
     * @since 0.0.3
     */
    public List<T> asList() {
        return this.race.asList();
    }

    /**
     * Action to perform.
     * @param <T> The type of result
     * @since 0.0.1
     */
    @FunctionalInterface
    public interface Action<T> {

        /**
         * Apply it.
         * @param thread The thread number
         * @return The result
         * @throws Exception If fails
         */
        T apply(int thread) throws Exception;
    }
}

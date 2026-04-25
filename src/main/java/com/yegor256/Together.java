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
 * @param <T> The type of result
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

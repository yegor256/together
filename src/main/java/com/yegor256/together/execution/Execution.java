/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.execution;

import com.yegor256.TogetherFailure;
import com.yegor256.together.failure.FailureContext;
import com.yegor256.together.failure.FailureKind;
import com.yegor256.together.failure.RaisedFailure;
import java.util.List;
import java.util.Map;

/**
 * One execution result.
 * @param <T> Type of result
 * @since 1.0
 */
public final class Execution<T> {

    /**
     * Thread number.
     */
    private final int thread;

    /**
     * Result.
     */
    private final T result;

    /**
     * Failure.
     */
    private final Exception failure;

    /**
     * Elapsed time.
     */
    private final long elapsed;

    /**
     * Ctor for success.
     * @param number Thread number
     * @param value Result
     * @param msec Elapsed
     */
    public Execution(final int number, final T value, final long msec) {
        // @checkstyle ParameterNumberCheck (1 line)
        this(number, value, null, msec);
    }

    /**
     * Ctor for failure.
     * @param number Thread number
     * @param problem Failure
     * @param msec Elapsed
     */
    public Execution(final int number, final Exception problem, final long msec) {
        // @checkstyle ParameterNumberCheck (1 line)
        this(number, null, problem, msec);
    }

    /**
     * Private ctor.
     * @param number Thread number
     * @param value Result
     * @param problem Failure
     * @param msec Elapsed
     */
    // @checkstyle ParameterNumberCheck (3 lines)
    private Execution(final int number, final T value, final Exception problem,
        final long msec) {
        this.thread = number;
        this.result = value;
        this.failure = problem;
        this.elapsed = msec;
    }

    /**
     * Put into the map.
     * @param all Mutable map
     */
    public void placeInto(final Map<Integer, Execution<T>> all) {
        all.put(this.thread, this);
    }

    /**
     * Stop fast if this execution failed.
     * @param round Round number
     * @param started Started execution
     */
    public void stopFastIn(final int round, final Started<T> started) {
        if (this.failure != null) {
            started.stop();
            throw this.failureIn(round);
        }
    }

    /**
     * Complete the round.
     * @param round Round number
     * @param results Mutable results
     */
    public void complete(final int round, final List<T> results) {
        if (this.failure != null) {
            throw this.failureIn(round);
        }
        results.add(this.result);
    }

    /**
     * Convert to public failure.
     * @param round Round number
     * @return Failure
     */
    public TogetherFailure failureIn(final int round) {
        return new RaisedFailure(
            new FailureContext(round, this.thread, this.elapsed),
            new FailureKind(false, this.failure.getMessage())
        ).asPublic(this.failure);
    }
}

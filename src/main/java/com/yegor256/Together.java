/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
     * Total number of threads to use.
     */
    private final int threads;

    /**
     * The action to perform.
     */
    private final Together.Action<T> action;

    /**
     * Ctor.
     *
     * <p>The number of threads here will be the maximum of current
     * available CPUs on the host machine and the number three. Thus, at
     * least three threads will be executed.</p>
     *
     * @param act The action
     */
    public Together(final Together.Action<T> act) {
        this(Math.max(Runtime.getRuntime().availableProcessors(), 3), act);
    }

    /**
     * Ctor.
     * @param total The number of threads
     * @param act The action
     */
    public Together(final int total, final Together.Action<T> act) {
        this.threads = total;
        this.action = act;
    }

    @Override
    @SuppressWarnings({"PMD.CloseResource", "PMD.DoNotThrowExceptionInFinally"})
    public Iterator<T> iterator() {
        final CountDownLatch latch = new CountDownLatch(1);
        final ExecutorService service =
            Executors.newFixedThreadPool(this.threads);
        try {
            final Map<Integer, Future<T>> futures =
                new HashMap<>();
            for (final int pos : new Shuffled(this.threads)) {
                futures.put(
                    pos,
                    service.submit(
                        () -> {
                            latch.await();
                            return this.action.apply(pos);
                        }
                    )
                );
            }
            latch.countDown();
            final Collection<T> rets = new LinkedList<>();
            for (int index = 0; index < this.threads; ++index) {
                try {
                    rets.add(futures.get(index).get());
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException(ex);
                } catch (final ExecutionException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
            return new Together.Iter<>(rets, rets.iterator());
        } finally {
            service.shutdown();
            try {
                if (!service.awaitTermination(1L, TimeUnit.MINUTES)) {
                    service.shutdownNow();
                    if (!service.awaitTermination(1L, TimeUnit.MINUTES)) {
                        throw new IllegalStateException("Can't shutdown");
                    }
                }
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalArgumentException(ex);
            }
        }
    }

    /**
     * Turn it into a list.
     * @return The list
     * @since 0.0.3
     */
    public List<T> asList() {
        final List<T> list = new LinkedList<>();
        for (final T item : this) {
            list.add(item);
        }
        return list;
    }

    /**
     * Action to perform.
     *
     * @param <T> The type of result
     * @since 0.0.1
     */
    public interface Action<T> {
        /**
         * Apply it.
         * @param thread The thread number
         * @return The result
         * @throws Exception If fails
         */
        T apply(int thread) throws Exception;
    }

    /**
     * The iterator.
     *
     * @param <T> The type of result
     * @since 0.0.2
     */
    private static final class Iter<T> implements Iterator<T> {
        /**
         * The list of items.
         */
        private final Collection<T> items;

        /**
         * The iterator.
         */
        private final Iterator<T> iterator;

        /**
         * Ctor.
         * @param list The list
         * @param iter The iterator
         */
        Iter(final Collection<T> list, final Iterator<T> iter) {
            this.items = list;
            this.iterator = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public T next() {
            return this.iterator.next();
        }

        @Override
        public String toString() {
            final StringBuilder text = new StringBuilder(0).append('[');
            for (final T item : this.items) {
                if (text.length() > 1) {
                    text.append(", ");
                }
                text.append(item);
            }
            return text.append(']').toString();
        }
    }

    /**
     * Shuffled sequence of integers.
     * @since 0.1.1
     */
    private static final class Shuffled implements Iterable<Integer> {
        /**
         * The size of items.
         */
        private final int size;

        Shuffled(final int size) {
            this.size = size;
        }

        @Override
        public Iterator<Integer> iterator() {
            final List<Integer> items = new ArrayList<>(this.size);
            for (int idx = 0; idx < this.size; ++idx) {
                items.add(idx);
            }
            Collections.shuffle(items);
            return items.iterator();
        }
    }
}

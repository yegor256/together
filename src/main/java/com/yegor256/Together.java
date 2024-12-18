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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Runs a lambda in multiple threads.
 *
 * @param <T> The type of result
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
     * @param act The action
     */
    public Together(final Together.Action<T> act) {
        this(Runtime.getRuntime().availableProcessors(), act);
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
            final Collection<Future<T>> futures =
                new ArrayList<>(this.threads);
            for (int pos = 0; pos < this.threads; ++pos) {
                final int thread = pos;
                futures.add(
                    service.submit(
                        () -> {
                            latch.await();
                            return this.action.apply(thread);
                        }
                    )
                );
            }
            latch.countDown();
            final Collection<T> rets = new LinkedList<>();
            for (final Future<T> future : futures) {
                try {
                    rets.add(future.get());
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalArgumentException(ex);
                } catch (final ExecutionException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
            return rets.iterator();
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
         */
        T apply(int thread);
    }
}

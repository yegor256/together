/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Executor shutdown.
 * @since 1.0
 */
public final class Shutdown {

    /**
     * Executor service.
     */
    private final ExecutorService service;

    /**
     * Ctor.
     * @param origin Service
     */
    public Shutdown(final ExecutorService origin) {
        this.service = origin;
    }

    /**
     * Shutdown it.
     */
    public void finish() {
        this.service.shutdown();
        try {
            if (!this.service.awaitTermination(1L, TimeUnit.MINUTES)) {
                this.service.shutdownNow();
                if (!this.service.awaitTermination(1L, TimeUnit.MINUTES)) {
                    throw new IllegalStateException("Can't shutdown");
                }
            }
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException(ex);
        }
    }
}

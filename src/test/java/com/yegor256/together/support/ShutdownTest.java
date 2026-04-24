/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Shutdown}.
 *
 * @since 1.0
 */
final class ShutdownTest {

    @Test
    void shutsExecutorDown() {
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            new Shutdown(service).finish();
            MatcherAssert.assertThat(
                "must shutdown executor",
                service.isShutdown(),
                Matchers.is(true)
            );
        }
    }

    @Test
    void failsOnInterruptedShutdown() {
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            service.submit(
                () -> {
                    Thread.sleep(1000L);
                    return 1;
                }
            );
            Thread.currentThread().interrupt();
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Shutdown(service).finish(),
                "must convert interrupted shutdown"
            );
            MatcherAssert.assertThat(
                "must preserve interrupted flag",
                Thread.currentThread().isInterrupted(),
                Matchers.is(true)
            );
            Thread.interrupted();
        }
    }
}

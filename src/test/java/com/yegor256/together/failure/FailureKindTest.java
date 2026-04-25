/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.failure;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link FailureKind}.
 * @since 1.0
 */
final class FailureKindTest {

    @Test
    void recognizesTimeoutAndMessage() {
        final FailureKind kind = new FailureKind(true, "boom");
        MatcherAssert.assertThat(
            "must keep timeout and message",
            kind.causedByTimeout() && kind.hasMessage(),
            Matchers.is(true)
        );
    }

    @Test
    void returnsMessageText() {
        MatcherAssert.assertThat(
            "must return root message",
            new FailureKind(false, "boom").messageText(),
            Matchers.equalTo("boom")
        );
    }

    @Test
    void recognizesRegularFailureWithoutMessage() {
        final FailureKind kind = new FailureKind(false, (String) null);
        MatcherAssert.assertThat(
            "must keep non-timeout empty state",
            kind.causedByTimeout() || kind.hasMessage(),
            Matchers.is(false)
        );
    }
}

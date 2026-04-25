/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.together.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Iterator with informative {@link #toString()}.
 * @param <T> Type of result
 * @since 1.0
 */
public final class Iter<T> implements java.util.Iterator<T> {

    /**
     * Items.
     */
    private final List<T> items;

    /**
     * Current position.
     */
    private int position;

    /**
     * Ctor.
     * @param all Items
     */
    public Iter(final Collection<T> all) {
        this.items = new ArrayList<>(all);
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return this.position < this.items.size();
    }

    @Override
    public T next() {
        this.position = this.position + 1;
        return this.items.get(this.position - 1);
    }

    @Override
    public String toString() {
        final StringBuilder text = new StringBuilder(16).append('[');
        for (final T item : this.items) {
            if (text.length() > 1) {
                text.append(", ");
            }
            text.append(item);
        }
        return text.append(']').toString();
    }
}

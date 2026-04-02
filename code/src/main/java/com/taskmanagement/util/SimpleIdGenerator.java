package com.taskmanagement.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates short sequential string identifiers for newly created objects.
 */
public final class SimpleIdGenerator {
    private static final AtomicLong COUNTER = new AtomicLong(0);

    private SimpleIdGenerator() {
    }

    public static String nextId() {
        return String.valueOf(COUNTER.incrementAndGet());
    }
}
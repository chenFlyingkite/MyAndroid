package com.flyingkite.library.logging;

@FunctionalInterface
public interface LogSS {
    void run(String tag, String message);
}

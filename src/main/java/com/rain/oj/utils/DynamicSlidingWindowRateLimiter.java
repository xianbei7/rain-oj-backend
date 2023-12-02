package com.rain.oj.utils;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.BlockingQueue;

public class DynamicSlidingWindowRateLimiter {
    private RateLimiter rateLimiter;
    private BlockingQueue<Long> timestampQueue;
    private Integer windowSize;

    public DynamicSlidingWindowRateLimiter(int initialPermitsPerWindow, int initialWindowSize) {
        this.windowSize = initialWindowSize;
        this.rateLimiter = RateLimiter.create(initialPermitsPerWindow);
        this.timestampQueue = Queues.newLinkedBlockingQueue(initialPermitsPerWindow);
    }

    public void updateRateLimit(int permitsPerSecond) {
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public boolean tryAcquire(long userId) {
        long currentTimestamp = System.currentTimeMillis();

        while (!timestampQueue.isEmpty() && timestampQueue.peek() <= currentTimestamp - windowSize * 1000) {
            timestampQueue.poll();
        }

        if (rateLimiter.tryAcquire()) {
            timestampQueue.offer(currentTimestamp);
            return true;
        }

        return false;
    }
}
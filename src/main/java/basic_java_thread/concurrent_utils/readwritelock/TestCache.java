package basic_java_thread.concurrent_utils.readwritelock;

/**
 * Created by guoyifeng on 10/14/19
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A simple implementation of cache using ReentrantReadWriteLock
 */
public class TestCache {

    static Map<String, Object> cache = new HashMap<>();

    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    static Lock r = rwl.readLock();

    static Lock w = rwl.writeLock();

    public static final Object get(String key) {
        r.lock();
        try {
            return cache.get(key);
        } finally {
            r.unlock();
        }
    }

    public static final void put(String key, Object value) {
        w.lock();
        try {
            cache.put(key, value);
        } finally {
            w.unlock();
        }
    }

    public static final void clear() {
        w.lock();
        try {
            cache.clear();
        } finally {
            w.unlock();
        }
    }
}

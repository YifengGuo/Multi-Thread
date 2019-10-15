package basic_java_thread.concurrent_utils.condition;

/**
 * Created by guoyifeng on 10/15/19
 */

import com.google.common.base.Preconditions;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of BlockingQueue using Condition await/signal policy
 */
public class BlockingQueue<T> {
    private Object[] items;
    // current new added/removed item cursor index, current total items count
    private int addIndex, removeIndex, count;
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public BlockingQueue(int initialSize) {
        Preconditions.checkArgument(initialSize > 0, "initial size must be positive");
        items = new Object[initialSize];
    }

    public void add(T t) throws InterruptedException {
        lock.lock();  // critical section
        try {
            // wait when queue is full
            while (count == items.length) {
                notFull.await();
            }
            items[addIndex] = t;
            if (++addIndex == items.length) {
                addIndex = 0;
            }
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T remove() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            Object t = items[removeIndex];
            if (++removeIndex == items.length) {
                removeIndex = 0;
            }
            --count;
            notFull.signal();
            return (T) t;
        } finally {
            lock.unlock();
        }
    }
}

package basic_java_thread.concurrent_utils.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by guoyifeng on 10/15/19
 */

/**
 * Condition offers an advanced edition of wait/notify policy of all objects
 */
public class ConditionUseCase {
    Lock lock = new ReentrantLock();

    Condition condition = lock.newCondition();

    public void conditionWait() {
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void conditionSignal() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

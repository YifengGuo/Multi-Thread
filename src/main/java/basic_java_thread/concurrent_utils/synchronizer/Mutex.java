package basic_java_thread.concurrent_utils.synchronizer;

/**
 * @author yifengguo
 */

/**
 * A simple impl of custom mutex lock
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;

/**
 * Synchronizer is the key to implementation on lock or any other synchronization modules.
 * This is achieved by holding a Synchronizer in the implementation of Lock.
 * The idea is:
 *      1. Lock is oriented to users. It defines interfaces for interactions between users and lock (e.g. allow multiple
 *         threads hold this lock like ReadWriteLock) and hide lock implementations to users
 *      2. Synchronizer is oriented to implementor of Lock. It simplifies the implementation of Lock, ignoring the
 *         low level ops like management of synchronized state, queue and wait/notify of threads
 */
public class Mutex {

    // holding inner customized Synchronizer
    private static class Sync extends AbstractQueuedSynchronizer {

        /**
         * if current currently hold the lock, if so state shall be 1
         * @return state == 1
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        /**
         * mutually acquire lock when state == 0
         * if the state of the object permits it to be acquired in the
         * exclusive mode, and if so to acquire it.
         *
         * This simple Mutex Lock does NOT support reentrant feature so if one thread
         * has acquired this lock successfully before, and try to acquire this lock again
         * this thread will be blocked
         * @param arg
         * @return
         */
        @Override
        protected boolean tryAcquire(int arg) {
            // CAS to set state in an atom operation
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true; // successfully mutually acquire the lock
            }
            return false;
        }

        /**
         * mutually release the lock and reset state back to 0
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        // return a Condition
        // each Condition obj contains a condition array
        Condition newCondition() {
            return new ConditionObject();
        }
    }

    // inner held Synchronizer obj of Lock
    private final Sync sync = new Sync();

    // exposed interfaces to users
    // simply proxied by inner Synchronizer
    public void lock() {
        sync.acquire(1);  // args have no special meaning, could be anything you like
    }

    public void tryLock() {
        sync.tryAcquire(1);
    }

    public void unlock() {
        sync.release(1);
    }

    public Condition newCondition() {
        return sync.newCondition();
    }

    public boolean isLocked() {
        return sync.isHeldExclusively();
    }

    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    public static void main(String[] args) throws Exception {
        Mutex mutex = new Mutex();
        DummyTask task1 = new DummyTask(mutex);
        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task1);
        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        task1.shutdown();
    }

    static class DummyTask implements Runnable {

        private static volatile boolean isRunning = true;

        private Mutex mutex;

        public DummyTask(Mutex mutex) {
            this.mutex = mutex;
        }

        @Override
        public void run() {
            int count = 0;
            while (isRunning) {
                mutex.lock();
                count++;
                System.out.println(mutex.hasQueuedThreads());
//                System.out.println(Thread.currentThread().getName() + ": " + count);
                mutex.unlock();
            }
        }

        public void shutdown() {
            isRunning = false;
        }
    }
}

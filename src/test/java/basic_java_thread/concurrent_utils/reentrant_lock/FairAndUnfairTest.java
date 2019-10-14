package basic_java_thread.concurrent_utils.reentrant_lock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by guoyifeng on 10/14/19
 */
public class FairAndUnfairTest {

    private static Lock fairLock = new ReentrantLock2(true);

    private static Lock unfairLock = new ReentrantLock2(false);

    @Test
    public void testFair() throws InterruptedException {
        testLock(fairLock);
    }

    @Test
    public void testUnfair() throws InterruptedException {
        testLock(unfairLock);
    }

    private void testLock(Lock lock) {
        for (int i = 0; i < 5; ++i) {
            new Thread(new Job(lock)).start();
        }
    }

    private static class Job implements Runnable {

        private Lock lock;

        public Job(Lock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " " + ((ReentrantLock2) lock).getQueuedThreads());
                    System.out.println(Thread.currentThread().getName() + " " + ((ReentrantLock2) lock).getQueuedThreads());
                } finally {
                    lock.unlock();
                }
            }
//            lock.lock();
//            System.out.println(Thread.currentThread().getName() + " " + ((ReentrantLock2) lock).getQueuedThreads());
//            System.out.println(Thread.currentThread().getName() + " " + ((ReentrantLock2) lock).getQueuedThreads());
//            lock.unlock();
        }
    }

    private static class ReentrantLock2 extends ReentrantLock {
        public ReentrantLock2(boolean fair) {
            super(fair);
        }

        /**
         * to reverse queued threads on current lock
         * @return
         */
        public Collection<Thread> getQueuedThreads() {
            List<Thread> threads = new ArrayList<>(super.getQueuedThreads());
            Collections.reverse(threads);
            return threads;
        }
    }
}

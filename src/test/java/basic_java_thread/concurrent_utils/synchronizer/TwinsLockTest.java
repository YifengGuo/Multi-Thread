package basic_java_thread.concurrent_utils.synchronizer;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author yifengguo
 */

public class TwinsLockTest {
    @Test
    public void test() throws InterruptedException {
        final Lock lock = new TwinsLock();
        DummyTask dummyTask = new DummyTask(lock);
        for (int i = 0; i < 10; ++i) {
            Thread t = new Thread(dummyTask);
            t.start();
        }

        for (int i = 0; i < 10; ++i) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println();
        }
    }

    class DummyTask implements Runnable {

        private Lock lock;

        public DummyTask(Lock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Current one of lock owner threads is " + Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}

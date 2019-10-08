package basic_java_thread.concurrent_utils;

/**
 * Created by guoyifeng on 4/24/19
 */

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Another demo of using CountDownLatch
 * Here is a pair of classes in which a group of worker threads use two countdown latches:
 *
 * The first is a start signal that prevents any worker from proceeding until the driver is ready for them to proceed;
 * The second is a completion signal that allows the driver to wait until all workers have completed.
 *
 * Assume we have one driver and several workers
 */
public class DemoCountDownLatch2 {
    private static final int WORKER_COUNT = 10;

    public static void main(String[] args) {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch completeSignal = new CountDownLatch(WORKER_COUNT);

        ExecutorService exec = Executors.newCachedThreadPool();

        exec.submit(new Driver(startSignal, completeSignal));

        for (int i = 0; i < WORKER_COUNT; ++i) {
            exec.submit(new Worker(startSignal, completeSignal));
        }

        if (completeSignal.getCount() == 0) {
            System.out.println("All job done");
        }
        exec.shutdown();
    }
}

class Driver implements Runnable {
    private final CountDownLatch startSignal;
    private final CountDownLatch completeSignal;
    private Random rand = new Random(67);

    public Driver(CountDownLatch startSignal, CountDownLatch completeSignal) {
        this.startSignal = startSignal;
        this.completeSignal = completeSignal;
    }

    @Override
    public void run() {
        try {
            doSomethingElse();
            System.out.println("Driver job done");
            startSignal.countDown(); // let all workers start working
            completeSignal.await();  // waiting for all the workers complete job
            System.out.println("All workers job done");
        } catch (InterruptedException e) {
            System.out.println("Driver exiting via interrupting");
        }
    }

    public void doSomethingElse() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(rand.nextInt(2000));
    }
}

class Worker implements Runnable {
    private final CountDownLatch startSignal;
    private final CountDownLatch completeSignal;
    private static int counter = 0;
    private final int id = counter++;
    private Random rand = new Random(67);

    public Worker(CountDownLatch startSignal, CountDownLatch completeSignal) {
        this.startSignal = startSignal;
        this.completeSignal = completeSignal;
    }

    @Override
    public void run() {
        try {
            // waiting for driver
            startSignal.await();
            doSomethingElse();  // worker working period
            System.out.println("Worker " + this + " job done");
            completeSignal.countDown();
        } catch (InterruptedException e) {
            System.out.println("Worker " + this + " exiting via interrupting");
        }
    }

    public void doSomethingElse() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(rand.nextInt(5000));
    }

    @Override
    public String toString() {
        return String.format("%1$-3d ", id);
    }
}

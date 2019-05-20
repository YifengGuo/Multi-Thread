package basic_java_thread.concurrent_utils;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 4/24/19
 */

/**
 * A demo for one usage of CountDownLatch
 * used under a situation when some tasks shall wait until
 * some other tasks complete. CountDownLatch could be used
 * to check if all prerequisites tasks are all complete or not
 * when CountDownLatch counts equals zero, waiting tasks shall begin
 */
public class DemoCountDownLatch1 {
    private static final int SIZE = 100;  // the count of prerequisites portions
    private static final int WAITING_SIZE = 10;  // the count of waiting tasks
    public static void main(String[] args) {
        // All must share a single CountDownLatch object
        CountDownLatch latch = new CountDownLatch(SIZE);
        ExecutorService exec = Executors.newCachedThreadPool();

        // initialize WaitingTask
        for (int i = 0; i < WAITING_SIZE; i++) {
            exec.submit(new WaitingTask(latch));
        }

        // initialize TaskPortion
        for (int i = 0; i < SIZE; i++) {
            exec.submit(new TaskPortion(latch));
        }

        System.out.println("All tasks launched");

        exec.shutdown();  // use shutdown() instead of shutdownNow() to make execution complete before interruption
    }
}

// performs some portion of a task
class TaskPortion implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private Random rand = new Random(66);
    private final CountDownLatch latch;

    public TaskPortion(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            doWork();
            latch.countDown();  // means a portion of a task is complete
                                // when all portions of this task complete, other waiting tasks can be started
        } catch (InterruptedException e) {
            System.out.println(this + " exiting via interrupting...");
        }
    }

    public void doWork() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(rand.nextInt(2000));
        System.out.println(this + " completed");
    }

    @Override
    public String toString() {
        return String.format("%1$-3d ", id);
    }
}

class WaitingTask implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private Random rand = new Random(66);
    private final CountDownLatch latch;

    public WaitingTask(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            latch.await();  // wait until all (prerequisites) portions done
            System.out.println("Latch barrier passed for " + this);
        } catch (InterruptedException e) {
            System.out.println("WaitingTask "+ this +"exiting via interrupting");
        }
    }

    @Override
    public String toString() {
        return String.format("%1$-3d ", id);
    }
}

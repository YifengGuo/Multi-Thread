package basic_java_thread.thread_pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 7/24/18
 */

/**
 * four kinds of thread pools in java.util.concurrent
 */
@SuppressWarnings("Duplicates")
public class FourThreadPool {
    public static void main(String[] args) {

        // cacheThreadPoolTest();

        // fixedThreadPoolTest();

        // scheduledThreadPoolTest1();

        // scheduledThreadPoolTest2();

        // singleThreadPoolTest();
    }

    /**
     * will create new thread when new job comes
     * idle thread will be killed after 60 seconds
     *
     * cached thread pool will collect idle threads if total count of threads is greater than real job need.
     * However, if it cannot find any idle threads to collect, it will create new threads instead
     */
    public static void cacheThreadPoolTest() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            try {
                Thread.sleep(index * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(index);
                }
            });
        }
    }

    /**
     * fixed number of threads will be created initially
     * idle threads will no be collected
     */
    public static void fixedThreadPoolTest() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3); // 3 threads work concurrently
        for (int i = 0; i < 10; i++) {
            final int index = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * execute each thread by 3 seconds delay
     */
    public static void scheduledThreadPoolTest1() {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("delay 3 seconds");
            }
        }, 3, TimeUnit.SECONDS);
    }

    /**
     * delay 1 seconds, and execute each thread every 3 seconds
     */
    public static void scheduledThreadPoolTest2() {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("delay 1 seconds, and execute every 3 seconds");
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    /**
     * there is only one thread in the thread pool
     * and it will execute each job one by one
     */
    public static void singleThreadPoolTest() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleThreadExecutor.execute(new Runnable() {
                public void run() {
                    try {
                        System.out.println(index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

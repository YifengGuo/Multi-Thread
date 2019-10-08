package basic_java_thread.demo_thinking_in_java.critical_section;

/**
 * Created by guoyifeng on 3/29/19
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Test sudden exit on task of threads
 */
public class OrnamentalGarden {
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.submit(new Entrance(i));
        }
        TimeUnit.SECONDS.sleep(3);
        Entrance.cancel();
        exec.shutdown();

        if (!exec.awaitTermination(250, TimeUnit.MILLISECONDS)) {
            System.out.println("Some tasks were not terminated before timeout.");
        }
        System.out.println("Total: " + Entrance.getTotalCount());
        System.out.println("Sum of Entrances: " + Entrance.sumEntrances());
    }
}

class Count {

    private int count = 0;

    private Random random = new Random(47);

    // if remove synchronized, the Total will not equal to Sum
    // because count is not volatile and is not in a critical section either
    public synchronized int increment() {
        int temp = count;
        if (random.nextBoolean()) {
            Thread.yield();  // yield half the time
        }
        return (count = ++temp);
    }

    public synchronized int value() { return count; }
}

class Entrance implements Runnable {

    private static Count count = new Count();

    private static List<Entrance> entrances = new ArrayList<>();  // entrances are stored in a static list so all threads
                                                                  // are accessing one list and sumEntrances() is always
                                                                  // getting the newest value of number by synchronized method

    private int number = 0; // read & write operation on number is always in a critical section or synchronized method

    private final int id;

    private static volatile boolean canceled = false;

    public Entrance(int id) {
        this.id = id;
        // keep this task in a list. Also prevents
        // garbage collection of dead tasks
        entrances.add(this);
    }

    public static void cancel() { canceled = true; }

    /**
     * run() is simply to increment count and number
     * then sleep for 100 ms
     */
    @Override
    public void run() {
        while (!canceled) {
            synchronized (this) {
                ++number;
            }
            System.out.println(this + " Total: " + count.increment());

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        System.out.println("Stopping " + this);
    }

    public synchronized int getValue() {
        return number;
    }

    @Override
    public String toString() {
        return "Entrance " + id + ": " + getValue();
    }

    public static int getTotalCount() {
        return count.value();
    }

    public static int sumEntrances() {
        int sum = 0;
        for (Entrance entrance : entrances) {
            sum += entrance.getValue();
        }
        return sum;
    }
}
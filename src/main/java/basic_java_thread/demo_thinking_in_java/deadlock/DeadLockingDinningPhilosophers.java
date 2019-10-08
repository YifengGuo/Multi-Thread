package basic_java_thread.demo_thinking_in_java.deadlock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 4/23/19
 */
public class DeadLockingDinningPhilosophers {

    public static final int PONDER_FACTOR = 10; // to see the deadlock happens quickly

    public static final long RUNNING_SECONDS = 50;

    public static final int PHILOSOPHER_COUNT = 5;  // count of chopsticks == count of philosophers

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();

        Chopstick[] chopsticks = new Chopstick[PHILOSOPHER_COUNT];

        // initialize chopsticks
        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            chopsticks[i] = new Chopstick();
        }

        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            exec.submit(new Philosopher(chopsticks[i], chopsticks[(i + 1) / PHILOSOPHER_COUNT], i, PONDER_FACTOR));
        }

        // running for a while
        TimeUnit.SECONDS.sleep(RUNNING_SECONDS);
        exec.shutdownNow();
    }
}

/**
 * output:
 * Philosopher 1 is meditating
 * Philosopher 2 is meditating
 * Philosopher 0 is meditating
 * Philosopher 3 is meditating
 * Philosopher 4 is meditating
 * Philosopher 1 grabbing the left chopstick
 * Philosopher 2 grabbing the left chopstick
 * Philosopher 3 grabbing the left chopstick
 * Philosopher 4 grabbing the left chopstick
 * Philosopher 0 grabbing the left chopstick
 *
 * deadlock happens due to each philosopher is waiting for the right chopstick which
 * is held by another philosopher who is also waiting for the left one he needs
 */

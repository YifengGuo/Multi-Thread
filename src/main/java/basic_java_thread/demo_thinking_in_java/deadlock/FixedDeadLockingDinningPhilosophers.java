package basic_java_thread.demo_thinking_in_java.deadlock;

/**
 * Created by guoyifeng on 4/23/19
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The four pre-conditions that deadlock needs
 * 1. Mutex condition: a mutual exclusive resource in the task (chopstick can only be used by one philosopher)
 * 2. At least one task is holding the mutex resource and waiting for another resource held by another task
 *    (A philosopher holding one chopstick and waiting for another one)
 * 3. Resources cannot be preempted (Philosophers can only wait)
 * 4. Waiting in the loop: first task is wait for the resource held by second task .... and last task is waiting for
 *    the resource held by the first task
 *
 * only when above 4 conditions are given, will there be a chance a deadlock happens
 *
 * Solutions to deadlock:
 *
 * general idea: break one or more conditions above in a parallel condition
 *
 *  1. simplest one: break the loop waiting process -> because each philosopher is taking left chopstick first and
 *                   and then second, meanwhile release left first and then right. So in this order, philosophers may
 *                   meet the situation that each one is having left chopstick and waiting for the right one.
 *                   The solution is to make last philosopher take right chopstick first and then left one. In this way
 *                   the last philosopher will never try to stop his neighbor grabbing the chopstick on his left side
 *                   (neighbor's right side)
 *
 *  2. Arbitrator solution: invoke a waiter  In order to pick up the forks, a philosopher must ask permission of the
 *                          waiter. The waiter gives permission to only one philosopher at a time until the
 *                          philosopher has picked up both of their forks. Putting down a fork is always allowed.
 *
 *  3. Resource hierarchy solution (proposed by Dijkstra)
 *
 *  4. Chandy/Misra solution
 *
 *  for more info: https://en.wikipedia.org/wiki/Dining_philosophers_problem
 */
@SuppressWarnings("Duplicates")
public class FixedDeadLockingDinningPhilosophers {
    public static final int PONDER_FACTOR = 10; // to see the deadlock happens quickly

    public static final long RUNNING_SECONDS = 30;

    public static final int PHILOSOPHER_COUNT = 5;  // count of chopsticks == count of philosophers

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();

        Chopstick[] chopsticks = new Chopstick[PHILOSOPHER_COUNT];

        // initialize chopsticks
        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            chopsticks[i] = new Chopstick();
        }

        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            if (i < PHILOSOPHER_COUNT - 1) {
                exec.submit(new Philosopher(chopsticks[i], chopsticks[(i + 1)], i, PONDER_FACTOR));
            } else {
                exec.submit(new Philosopher(chopsticks[0], chopsticks[i], i, PONDER_FACTOR));
            }
        }

        // running for a while
        TimeUnit.SECONDS.sleep(RUNNING_SECONDS);
        exec.shutdownNow();
    }
}

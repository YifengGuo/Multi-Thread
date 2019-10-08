package basic_java_thread.demo_thinking_in_java.deadlock;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoyifeng on 4/23/19
 */
public class Philosopher implements Runnable {
    private Chopstick left;
    private Chopstick right;
    private int id;
    private int ponderFactor;
    private Random rand = new Random(66);

    /**
     *
     * @param left chopstick set on the left side of philosopher
     * @param right chopstick set on the right side of philosopher
     * @param id philosopher id
     * @param ponderFactor the factor of pondering time of philosopher, the bigger, the longer the ponder time
     */
    public Philosopher(Chopstick left, Chopstick right, int id, int ponderFactor) {
        this.left = left;
        this.right = right;
        this.id = id;
        this.ponderFactor = ponderFactor;
    }

    public void pause() throws InterruptedException {
        if (ponderFactor == 0) {
            return;
        }
        TimeUnit.MILLISECONDS.sleep(ponderFactor * rand.nextInt(500));
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println(this + " " + "is meditating");

                // meditating
                pause();

                // gets hungary
                left.take();
                System.out.println(this + " " + "grabbing the left chopstick");
                right.take();
                System.out.println(this + " " + "grabbing the right chopstick");

                // eating
                System.out.println(this + " " + "is eating");
                pause();
                System.out.println(this + " " + "finishes eating");

                // release chopsticks
                left.release();
                right.release();
            }
        } catch (InterruptedException e) {
            System.out.println(this + " exiting via interrupting...");
        }
    }

    @Override
    public String toString() {
        return "Philosopher " + id;
    }
}

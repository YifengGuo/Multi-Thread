package basic_java_thread.demo_thinking_in_java.deadlock;

/**
 * Created by guoyifeng on 4/23/19
 */
public class Chopstick {
    private boolean taken;  // flag bit to represent if the chopstick is taken by one philosopher or not

    public synchronized void take() throws InterruptedException {  // one chopstick can only be used by one philosopher at one time
        while (taken) {
            wait();
        }
        taken = true;
    }

    public synchronized void release() throws InterruptedException {
        taken = false;
        notifyAll(); // notify all the philosophers who are wait for this chopstick
    }
}

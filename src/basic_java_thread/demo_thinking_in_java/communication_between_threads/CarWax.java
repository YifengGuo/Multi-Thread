package basic_java_thread.demo_thinking_in_java.communication_between_threads;

/**
 * Created by guoyifeng on 4/22/19
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demo for wait(), notify() and notifyAll() in java
 * notifyAll() will only wake up threads who are waiting for the same lock (current object who invokes notifyAll())
 * Unlike Thread.sleep() and Thread.yield(), wait() will RELEASE the lock current thread was once holding
 * Car need to be waxed on after polished
 * And waxing another coat must wait for polishing
 */
public class CarWax {
    public static void main(String[] args) throws InterruptedException {
        Car car = new Car();  // car is the common lock for two tasks
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(new WaxOn(car));
        exec.submit(new Polish(car));
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}

class Car {
    private boolean waxOn = false;

    public synchronized void waxed() {
        waxOn = true;  // wax in done, ready to polish
        notifyAll();
    }

    public synchronized void polished() {
        waxOn = false;  // polishing is done, ready for another coat of wax
        notifyAll();
    }

    public synchronized void waitingForWax() throws InterruptedException {
        while (!waxOn) {
            wait();
        }
    }

    public synchronized void waitingForPolish() throws InterruptedException {
        while (waxOn) {
            wait();
        }
    }
}

class WaxOn implements Runnable {
    private Car car;

    public WaxOn(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("Wax On! ");
                TimeUnit.MILLISECONDS.sleep(200);
                car.waxed();
                car.waitingForPolish();
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupting...");
        }
        System.out.println("Ending Wax on task.");
    }
}

class Polish implements Runnable {
    private Car car;

    public Polish(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                car.waitingForWax();  // polishing can only possibly be done after waxing is done
                                      // so if wax on never happens, polishing will be blocked
                System.out.println("Polished! ");
                TimeUnit.MILLISECONDS.sleep(200);
                car.polished();
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupting...");
        }
        System.out.println("Ending Polishing task.");
    }
}
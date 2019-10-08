package basic_java_thread.demo_thinking_in_java.condition_lock;

/**
 * Created by guoyifeng on 4/22/19
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demo for lock.lock(), lock.unlock(), condition.await() and condition.signalAll() in java
 * notifyAll() will only wake up threads who are waiting for the same lock (current object who invokes notifyAll())
 * Unlike Thread.sleep() and Thread.yield(), wait() will RELEASE the lock current thread was once holding
 * Car need to be waxed on after polished
 * And waxing another coat must wait for polishing
 *
 * condition is used for managing communication between threads and
 * cannot record state info so we need boolean variable waxOn
 */
@SuppressWarnings("Duplicates")
public class CarWaxUsingLock {
    public static void main(String[] args) throws InterruptedException {
        Car car = new Car();  // now car has its own lock
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(new WaxOn(car));
        exec.submit(new Polish(car));
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}

class Car {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean waxOn = false;

    public void waxed() {
        lock.lock();
        try {
            waxOn = true;  // wax is done, ready to polish
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void polished() {
        lock.lock();
        try {
            waxOn = false;  // polishing is done, ready for another coat of wax
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitingForWax() throws InterruptedException {
        lock.lock();
        try {
            while (!waxOn) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void waitingForPolish() throws InterruptedException {
        lock.lock();
        try {
            while (waxOn) {
                condition.await();
            }
        } finally {
            lock.unlock();
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
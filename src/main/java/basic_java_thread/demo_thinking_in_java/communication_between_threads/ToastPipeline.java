package basic_java_thread.demo_thinking_in_java.communication_between_threads;

/**
 * Created by guoyifeng on 4/23/19
 */

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * A toast machine has three tasks:
 *  1. produce toast
 *  2. put butter on the toast
 *  3. after butter put on, put jam on the toast
 *
 * We will use BlockingQueue to simulate the process
 */
public class ToastPipeline {
    public static void main(String[] args) throws Exception {
        ToastQueue dryQueue = new ToastQueue();
        ToastQueue butteredQueue = new ToastQueue();
        ToastQueue jammedQueue = new ToastQueue();

        Toaster toaster = new Toaster(dryQueue);
        Butterer butterer = new Butterer(dryQueue, butteredQueue);
        Jammer jammer = new Jammer(butteredQueue, jammedQueue);
        Customer customer = new Customer(jammedQueue);

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(toaster);
        exec.submit(butterer);
        exec.submit(jammer);
        exec.submit(customer);

        TimeUnit.SECONDS.sleep(5);

        exec.shutdownNow();
    }
}

class Toast {
    public enum Status {DRY, BUTTERED, JAMMED}
    private Status status = Status.DRY; // initial status of toast is DRY
    private final int id;

    public Toast(int id) {
        this.id = id;
    }

    public void butter() {
        status = Status.BUTTERED;
    }

    public void jam() {
        status = Status.JAMMED;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Toast " + id + ": " + status;
    }
}

class ToastQueue extends LinkedBlockingDeque<Toast> {}

// Toaster is to produce toast and send to butter task
class Toaster implements Runnable {
    private ToastQueue tq;
    private int count;
    private Random rand = new Random(47);

    public Toaster(ToastQueue tq) {
        this.tq = tq;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.MILLISECONDS.sleep(100 + rand.nextInt(500)); // time cost for producing toast
                Toast toast = new Toast(count++);
                System.out.println(toast);
                // insert into queue
                tq.put(toast);
            }
        } catch (InterruptedException e) {
            System.out.println("Toaster exiting via interrupting...");
        }
        System.out.println("Toaster Off");
    }
}

class Butterer implements Runnable {
    private ToastQueue dryQueue;  // get toast from toaster
    private ToastQueue butteredQueue;  // produce toast for jam task

    public Butterer(ToastQueue dryQueue, ToastQueue butteredQueue) {
        this.dryQueue = dryQueue;
        this.butteredQueue = butteredQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // get toast and put butter on that
                Toast curr = dryQueue.take();
                curr.butter();
                System.out.println(curr);
                butteredQueue.put(curr);
            }
        } catch (InterruptedException e) {
            System.out.println("Butterer exiting via interrupting...");
        }
        System.out.println("Butterer Off");
    }
}

class Jammer implements Runnable {
    private ToastQueue butteredQueue;  // contains buttered toasts made by butterer
    private ToastQueue jammedQueue;

    public Jammer(ToastQueue butteredQueue, ToastQueue jammedQueue) {
        this.butteredQueue = butteredQueue;
        this.jammedQueue = jammedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast curr = butteredQueue.take();
                curr.jam();
                System.out.println(curr);
                jammedQueue.put(curr);
            }
        } catch (InterruptedException e) {
            System.out.println("Jammer exiting via interrupting...");
        }
        System.out.println("Jammer Off");
    }
}

// customer will only consume jammed toast
class Customer implements Runnable {
    private ToastQueue tq;

    public Customer(ToastQueue tq) {
        this.tq = tq;
    }

    private int count = 0;

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast curr = tq.take();
                if (curr.getId() != count++ || curr.getStatus() != Toast.Status.JAMMED) {
                    System.out.println("Wrong Toast....");
                    System.exit(-1);
                } else {
                    System.out.println(curr + " is eaten...");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Customer stopping eatting due to interruption...");
        }
        System.out.println("Customer Off");
    }
}

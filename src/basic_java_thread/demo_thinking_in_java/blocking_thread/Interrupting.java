package basic_java_thread.demo_thinking_in_java.blocking_thread;

/**
 * Created by guoyifeng on 3/29/19
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * demo for interrupting every kind of blocking condition on threads
 *
 * Conclusion: you can simply interrupt a thread from sleeping block
 *             but you can never interrupt a thread from I/O block or
 *             synchronized blocked (waiting for the access to critical section or methods)
 *
 *  To release the block on I/O, one can try to close resources of that blocked I/O
 *  Better implementation is available in java nio
 */
public class Interrupting {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    static void test(Runnable r) throws Exception {
        Future<?> future = exec.submit(r);  // Future has context of this thread and one can use Future to interrupt the thread
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Interrupting " + r.getClass().getName());
        future.cancel(true);  // interrupt the task while it is running
                                                // this cannot cancle the threads which are not running, were complete or for some reason cannot be cancelled
        System.out.println("Interrupt sent to blocked thread " + r.getClass().getName());
    }

    public static void main(String[] args) throws Exception {
        test(new SleepBlocked());
        test(new IOBlocked(System.in));
        test(new SynchronizedBlocked());
        TimeUnit.SECONDS.sleep(3);
        System.out.println("Aborting with System.exit(0)");
        System.exit(0);  // since last two methods can impossibly be interrupted
    }

}

class SleepBlocked implements Runnable {
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Catch InterruptedException");
        }
        System.out.println("Exiting SleepBlocked.run()");
    }
}

class IOBlocked implements Runnable {
    private InputStream in;

    public IOBlocked(InputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting for read():");
            in.read();
        } catch (IOException e) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Interrupted from blocked I/O");
            } else {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Exiting IOBlocked.run()");
    }
}

class SynchronizedBlocked implements Runnable {
    public synchronized void f() {
        Thread.yield();  // never release the lock
    }

    public SynchronizedBlocked() {
        new Thread(() -> f()).start();  // lock acquired by thread which invoked f()
    }

    @Override
    public void run() {  // so SynchronizedBlocked will be blocked and wait for the lock on f() forever
        System.out.println("Trying to call f()");
        f();
        System.out.println("Exiting SynchronizedBlocked.run()");
    }
}
